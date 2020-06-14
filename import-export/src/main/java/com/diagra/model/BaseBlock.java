package com.diagra.model;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseBlock implements Block {

    private final BlockType blockType;
    private final List<String> text;
    private Map<String, String> metaInfo = new HashMap<>();

    public BaseBlock(BlockType blockType, List<String> text) {
        this.blockType = blockType;
        this.text = ImmutableList.copyOf(text);
    }

    public BaseBlock(BlockType blockType, List<String> text, Map<String, String> metaInfo) {
        this(blockType, text);
        this.metaInfo = metaInfo;
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

    @Override
    public Map<String, String> metaInfo() {
        return this.metaInfo;
    }

}
