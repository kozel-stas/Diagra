package com.diagra;

import com.diagra.source.java.JavaSourceConverterService;
import com.diagra.xml.MXGraphXmlConverter;
import com.diagra.xml.XmlRepresentationConverter;
import com.google.common.collect.ImmutableList;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

@Service
public class ImportExportManagerImpl implements ImportExportManager {

    private final List<ConverterService> services;
    private final Graph<ResourceType, ServiceEdge> graph;

    public ImportExportManagerImpl(XmlRepresentationConverter xmlRepresentationConverter, JavaSourceConverterService javaSourceConverterService, MXGraphXmlConverter mxGraphXmlConverter) {
        this.services = ImmutableList.of(javaSourceConverterService, mxGraphXmlConverter, xmlRepresentationConverter);
        this.graph = new AsSynchronizedGraph<>(constructGraph(services));
    }

    @Override
    public Resource convert(Resource resource, ResourceType targetType) throws IEException {
        Resource result = resource;
        List<ConverterService> services = serviceChain(resource.getResourceType(), targetType);
        if (services.isEmpty()) {
            throw new IllegalStateException("Resource can't be converted from " + resource.getResourceType() + " to " + targetType);
        }
        for (ConverterService service : services) {
            result = service.convert(result);
        }
        return result;
    }

    @Nonnull
    private List<ConverterService> serviceChain(ResourceType source, ResourceType target) throws IllegalStateException {
        List<ConverterService> chain = new LinkedList<>();
        ShortestPathAlgorithm<ResourceType, ServiceEdge> shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<ResourceType, ImportExportManagerImpl.ServiceEdge> graphPath = shortestPath.getPath(source, target);
        if (graphPath == null) {
            return chain;
        }
        for (ServiceEdge serviceEdge : graphPath.getEdgeList()) {
            chain.add(serviceEdge.getService());
        }
        return chain;
    }

    private static Graph<ResourceType, ServiceEdge> constructGraph(List<ConverterService> services) {
        Graph<ResourceType, ServiceEdge> graph = new DefaultDirectedGraph<>(ServiceEdge.class);
        for (ConverterService service : services) {
            graph.addVertex(service.inputType());
            graph.addVertex(service.outputType());
            graph.addEdge(service.inputType(), service.outputType()).setService(service);
        }
        return graph;
    }

    public static class ServiceEdge extends DefaultEdge {

        private ConverterService service;

        public void setService(ConverterService service) {
            this.service = service;
        }

        public ConverterService getService() {
            return service;
        }

    }

}
