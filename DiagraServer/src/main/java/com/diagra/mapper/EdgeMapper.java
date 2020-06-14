package com.diagra.mapper;

import com.diagra.model.BaseEdge;
import com.diagra.model.Edge;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

@Service
public class EdgeMapper implements EntityMapper<Edge, com.diagra.dao.model.Edge> {

    private final BlockMapper blockMapper;
    private final EdgeTypeMapper edgeTypeMapper;

    public EdgeMapper(BlockMapper blockMapper, EdgeTypeMapper edgeTypeMapper) {
        this.blockMapper = blockMapper;
        this.edgeTypeMapper = edgeTypeMapper;
    }

    @Override
    public Edge toDto(com.diagra.dao.model.Edge entity) {
        return new BaseEdge(
                edgeTypeMapper.toDto(entity.edgeType()),
                entity.text(),
                blockMapper.toDto(entity.source()),
                blockMapper.toDto(entity.target())
        );
    }

    @Override
    public com.diagra.dao.model.Edge fromDto(Edge dto) throws IOException {
        return new com.diagra.dao.model.BaseEdge(
                edgeTypeMapper.fromDto(dto.edgeType()),
                dto.text(),
                blockMapper.fromDto(dto.source()),
                blockMapper.fromDto(dto.target()),
                new HashMap<>()
        );
    }

}