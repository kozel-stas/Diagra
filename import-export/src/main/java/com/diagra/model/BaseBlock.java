package com.diagra.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BaseBlock implements Block {

    private final BlockType blockType;
    private final List<String> text;

    public BaseBlock(BlockType blockType, List<String> text) {
        this.blockType = blockType;
        this.text = ImmutableList.copyOf(text);
    }

    @Override
    public String getText() {
        return String.join("\n", text);
    }

    @Override
    public List<String> commands() {
        return text;
    }

    @Override
    public BlockType blockType() {
        return blockType;
    }

}
