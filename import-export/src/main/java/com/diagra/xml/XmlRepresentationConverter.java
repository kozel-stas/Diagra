package com.diagra.xml;

import com.diagra.ConverterService;
import com.diagra.IEException;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.model.*;
import com.diagra.mxgraph.MxGraphModel;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class XmlRepresentationConverter implements ConverterService {

    JAXBContext jaxbContext = JAXBContext.newInstance(MxGraphModel.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    public XmlRepresentationConverter() throws JAXBException {
    }

    @Override
    public ResourceType outputType() {
        return ResourceType.INNER_REPRESENTATION;
    }

    @Override
    public ResourceType inputType() {
        return ResourceType.XML_MXGRAPH;
    }

    @Override
    public Resource convert(Resource resource) throws IEException {
        MxGraphModel object;
        try {
            object = (MxGraphModel) unmarshaller.unmarshal(IOUtils.toInputStream(resource.getObject().toString(), StandardCharsets.UTF_8));
        } catch (JAXBException e) {
            throw new IEException(e.getMessage());
        }

        List<Edge> edges = new LinkedList<>();
        Map<Integer, Block> mapping = new HashMap<>();
        for (MxGraphModel.Root.MxCell cell : object.getRoot().getMxCell()) {
            if (cell.getEdge() == null || cell.getEdge() == 0) {
                MxGraphProperty mx = MxGraphProperty.valueOfByStyle(cell.getStyle());
                if (mx == null) {
                    continue;
                }
                if (cell.getValue() == null){
                    cell.setValue("");
                }
                Map<String, String> map = new HashMap<>();
                if (cell.getMxGeometry() != null) {
                    MxGraphModel.Root.MxCell.MxGeometry mxGeometry = cell.getMxGeometry();
                    put(map, "height", mxGeometry.getHeight());
                    put(map, "width", mxGeometry.getWidth());
                    put(map, "x", mxGeometry.getX());
                    put(map, "y", mxGeometry.getY());
                }
                mapping.put(
                        cell.getId(),
                        new BaseBlock(
                                mx.getBlockType(),
                                Arrays.asList(cell.getValue().split("\n")),
                                map
                        )
                );
            }
        }

        for (MxGraphModel.Root.MxCell cell : object.getRoot().getMxCell()) {
            if (cell.getEdge() != null && cell.getEdge() != 0) {
                EdgeType edgeType;
                if (cell.getStyle() != null && cell.getStyle().contains("dashed")) {
                    edgeType = EdgeType.DOTTED_LINE;
                } else {
                    edgeType = EdgeType.LINE;
                }
                edges.add(
                        new BaseEdge(
                                edgeType,
                                cell.getValue(),
                                mapping.get(cell.getSource()),
                                mapping.get(cell.getTarget())
                        )
                );
            }
        }

        return new Resource(
                ResourceType.INNER_REPRESENTATION,
                new AlgorithmScheme("CONVERTED", new ArrayList<>(mapping.values()), edges)
        );
    }

    private static void put(Map<String, String> map, String key, Object data){
        if (data != null){
            map.put(key, data + "");
        }
    }

}