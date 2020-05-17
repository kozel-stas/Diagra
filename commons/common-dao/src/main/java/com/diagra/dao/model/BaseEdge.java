package com.diagra.dao.model;

import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.Map;

public class BaseEdge implements Edge, MetaInfo {

    private EdgeType edgeType;
    private String text;
    private Block source;
    private Block target;
    private Map<String, String> metaInfo;

    public BaseEdge() {
    }

    @PersistenceConstructor
    public BaseEdge(EdgeType edgeType, String text, Block source, Block target, Map<String, String> metaInfo) {
        setEdgeType(edgeType);
        setText(text);
        setSource(source);
        setTarget(target);
        setMetaInfo(metaInfo);
    }

    @Override
    public EdgeType edgeType() {
        return edgeType;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public Block source() {
        return source;
    }

    @Override
    public Block target() {
        return target;
    }

    public void setTarget(Block target) {
        this.target = target;
    }

    public void setSource(Block source) {
        this.source = source;
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Map<String, String> metaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(Map<String, String> metaInfo) {
        this.metaInfo = new HashMap<>(metaInfo);
    }

}
