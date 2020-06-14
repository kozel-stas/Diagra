package com.diagra.mapper;

import com.diagra.model.Block;
import com.diagra.model.BaseBlock;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class BlockMapper implements EntityMapper<Block, com.diagra.dao.model.Block> {

    private final BlockTypeMapper blockTypeMapper;

    public BlockMapper(BlockTypeMapper blockTypeMapper) {
        this.blockTypeMapper = blockTypeMapper;
    }

    @Override
    public Block toDto(com.diagra.dao.model.Block entity) {
        return new BaseBlock(
                blockTypeMapper.toDto(entity.blockType()),
                entity.getText(),
                entity.metaInfo()
        );
    }

    @Override
    public com.diagra.dao.model.Block fromDto(Block dto) throws IOException {
        return new com.diagra.dao.model.BaseBlock(
                blockTypeMapper.fromDto(dto.blockType()),
                dto.commands(),
                dto.metaInfo()
        );
    }

}
