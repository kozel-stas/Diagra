package com.diagra.dao.model;

import org.springframework.data.annotation.PersistenceConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseBlock implements Block, MetaInfo {

    private String id;
    private BlockType blockType;
    private List<String> text;
    private Map<String, String> metaInfo;

    public BaseBlock() {

    }

    @PersistenceConstructor
    public BaseBlock(BlockType blockType, List<String> text, Map<String, String> metaInfo) {
        setMetaInfo(metaInfo);
        setBlockType(blockType);
        setText(text);
    }

    @Override
    public List<String> getText() {
        return text;
    }

    @Override
    public BlockType blockType() {
        return blockType;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }

    public void setText(List<String> text) {
        this.text = new ArrayList<>(text);
    }

    public void setMetaInfo(Map<String, String> metaInfo) {
        this.metaInfo = new HashMap<>(metaInfo);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Map<String, String> metaInfo() {
        return metaInfo;
    }

}
