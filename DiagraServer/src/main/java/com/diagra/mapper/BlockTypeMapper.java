package com.diagra.mapper;

import com.diagra.model.Block;
import com.diagra.model.BlockType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import java.io.IOException;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BlockTypeMapper implements EntityMapper<BlockType, com.diagra.dao.model.BlockType> {

    @ValueMapping(source = "PROCESS", target = "PROCESS")
    @ValueMapping(source = "PREDEFINED_PROCESS", target = "PREDEFINED_PROCESS")
    @ValueMapping(source = "DATA", target = "DATA")
    @ValueMapping(source = "PREPARATION", target = "PREPARATION")
    @ValueMapping(source = "CYCLE_START", target = "CYCLE_START")
    @ValueMapping(source = "CYCLE_END", target = "CYCLE_END")
    @ValueMapping(source = "TERMINATOR", target = "TERMINATOR")
    @ValueMapping(source = "COMMENT", target = "COMMENT")
    @ValueMapping(source = "CONNECTOR", target = "CONNECTOR")
    @Override
    public abstract com.diagra.dao.model.BlockType fromDto(BlockType dto) throws IOException;

    @ValueMapping(source = "PROCESS", target = "PROCESS")
    @ValueMapping(source = "PREDEFINED_PROCESS", target = "PREDEFINED_PROCESS")
    @ValueMapping(source = "DATA", target = "DATA")
    @ValueMapping(source = "PREPARATION", target = "PREPARATION")
    @ValueMapping(source = "CYCLE_START", target = "CYCLE_START")
    @ValueMapping(source = "CYCLE_END", target = "CYCLE_END")
    @ValueMapping(source = "TERMINATOR", target = "TERMINATOR")
    @ValueMapping(source = "COMMENT", target = "COMMENT")
    @ValueMapping(source = "CONNECTOR", target = "CONNECTOR")
    @Override
    public abstract BlockType toDto(com.diagra.dao.model.BlockType entity);

}
