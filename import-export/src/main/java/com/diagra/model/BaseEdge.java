package com.diagra.model;

public class BaseEdge implements Edge {

    private final EdgeType edgeType;
    private final String text;
    private final Block source;
    private final Block target;

    public BaseEdge(EdgeType edgeType, String text, Block source, Block target) {
        this.edgeType = edgeType;
        this.text = text;
        this.source = source;
        this.target = target;
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

}
