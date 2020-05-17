package com.diagra.mapper;

import com.diagra.model.Edge;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.io.IOException;

//@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EdgeMapper implements EntityMapper<Edge, com.diagra.dao.model.Edge> {

    @Override
    public abstract Edge toDto(com.diagra.dao.model.Edge entity);

    @Override
    public abstract com.diagra.dao.model.Edge fromDto(Edge dto) throws IOException;

}