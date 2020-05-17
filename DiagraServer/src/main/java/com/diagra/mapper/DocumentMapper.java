package com.diagra.mapper;

import com.diagra.ImportExportManager;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.TransitionService;
import com.diagra.dao.model.AlgorithmScheme;
import com.diagra.dto.DocumentDto;
import com.diagra.utils.TransitionUtil;

import java.io.IOException;

//@Service
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
        return null;
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
//
//        final Map<Block, >
//
//        return new AlgorithmScheme(
//                null,
//                dto.getName(),
//
//                );

        return null;
    }

}
