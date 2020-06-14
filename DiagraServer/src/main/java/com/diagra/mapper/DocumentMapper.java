package com.diagra.mapper;

import com.diagra.ImportExportManager;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.TransitionService;
import com.diagra.dao.model.AlgorithmScheme;
import com.diagra.dao.model.BaseEdge;
import com.diagra.dto.DocumentDto;
import com.diagra.dto.IEType;
import com.diagra.model.Block;
import com.diagra.model.Edge;
import com.diagra.utils.TransitionUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class DocumentMapper implements EntityMapper<DocumentDto, AlgorithmScheme> {

    private final TransitionService transitionService;
    private final ImportExportManager importExportManager;
    private final BlockMapper blockMapper;
    private final EdgeMapper edgeMapper;

    public DocumentMapper(
            TransitionService transitionService,
            ImportExportManager importExportManager,
            BlockMapper blockMapper,
            EdgeMapper edgeMapper
    ) {
        this.transitionService = transitionService;
        this.importExportManager = importExportManager;
        this.blockMapper = blockMapper;
        this.edgeMapper = edgeMapper;
    }

    @Override
    public DocumentDto toDto(AlgorithmScheme entity) {
        List<Edge> edges = new ArrayList<>();
        Map<String, Block> mapping = new HashMap<>();
        for (com.diagra.dao.model.Edge edge : entity.getEdges()) {
            Edge edge1 = edgeMapper.toDto(edge);
            mapping.putIfAbsent(edge.source().getId(), edge1.source());
            mapping.putIfAbsent(edge.target().getId(), edge1.target());
            edges.add(new com.diagra.model.BaseEdge(
                    edge1.edgeType(),
                    edge1.text(),
                    mapping.get(edge.source().getId()),
                    mapping.get(edge.target().getId())
            ));
        }

        List<Block> blocks = new ArrayList<>(mapping.values());
        for (com.diagra.dao.model.Block block : entity.getBlocks()) {
            blocks.add(blockMapper.toDto(block));
        }

        com.diagra.model.AlgorithmScheme alg = new com.diagra.model.AlgorithmScheme(entity.getName(), blocks, edges);

        Resource inner = importExportManager.convert(
                new Resource(
                        ResourceType.INNER_REPRESENTATION,
                        alg
                ),
                ResourceType.XML_MXGRAPH
        );

        String link = transitionService.generateTransitionLink(new TransitionService.Resource(
                entity.getOwnerID(),
                MediaType.TEXT_XML,
                ResourceType.XML_MXGRAPH.toResource(inner.getObject())
        ));

        DocumentDto documentDto = new DocumentDto();
        documentDto.setDescription(entity.getDescription());
        documentDto.setName(entity.getName());
        documentDto.setUserID(entity.getOwnerID());
        documentDto.setType(IEType.XML);
        documentDto.setId(entity.getId());
        documentDto.setTransitionLink(TransitionUtil.generateTransitionLink(link));
        return documentDto;
    }

    @Override
    public AlgorithmScheme fromDto(DocumentDto dto) throws IOException {
        TransitionService.Resource resource = transitionService.load(
                TransitionUtil.subTransitionLink(dto.getTransitionLink()),
                dto.getUserID()
        );

        Resource inner = importExportManager.convert(
                new Resource(
                        dto.getType().getResourceType(),
                        dto.getType().getResourceType().fromResource(resource.getResource())
                ),
                ResourceType.INNER_REPRESENTATION
        );
        com.diagra.model.AlgorithmScheme alg = (com.diagra.model.AlgorithmScheme) inner.getObject();

        Map<Block, com.diagra.dao.model.Block> mapping = new HashMap<>();
        List<com.diagra.dao.model.Edge> edges = new ArrayList<>(alg.getEdges().size());
        for (Edge edge : alg.getEdges()) {
            com.diagra.dao.model.Edge converted = edgeMapper.fromDto(edge);
            mapping.putIfAbsent(edge.source(), converted.source());
            mapping.putIfAbsent(edge.target(), converted.target());
            edges.add(
                    new BaseEdge(
                            converted.edgeType(),
                            converted.text(),
                            mapping.get(edge.source()),
                            mapping.get(edge.target()),
                            converted.metaInfo()
                    )
            );
        }

        List<Block> blocks = new ArrayList<>(alg.getBlocks());
        List<com.diagra.dao.model.Block> daoBlock = new ArrayList<>();
        blocks.removeAll(mapping.keySet());
        for (Block block : blocks) {
            daoBlock.add(blockMapper.fromDto(block));
        }
        return new AlgorithmScheme(
                null,
                dto.getName(),
                daoBlock,
                edges,
                null,
                new HashMap<>(),
                dto.getDescription()
        );
    }

}
