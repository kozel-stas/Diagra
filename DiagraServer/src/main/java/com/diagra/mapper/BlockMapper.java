package com.diagra.mapper;

import com.diagra.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.io.IOException;

//@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BlockMapper implements EntityMapper<Block, com.diagra.dao.model.Block> {

    @Override
    public abstract Block toDto(com.diagra.dao.model.Block entity);

    @Override
    public abstract com.diagra.dao.model.Block fromDto(Block dto) throws IOException;

}
