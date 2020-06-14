package com.diagra.mapper;

import com.diagra.model.EdgeType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EdgeTypeMapper implements EntityMapper<EdgeType, com.diagra.dao.model.EdgeType> {

    @ValueMapping(source = "LINE", target = "LINE")
    @ValueMapping(source = "DOTTED_LINE", target = "DOTTED_LINE")
    @Override
    public abstract EdgeType toDto(com.diagra.dao.model.EdgeType entity);

    @ValueMapping(source = "LINE", target = "LINE")
    @ValueMapping(source = "DOTTED_LINE", target = "DOTTED_LINE")
    @Override
    public abstract com.diagra.dao.model.EdgeType fromDto(EdgeType dto) throws IOException;

}
