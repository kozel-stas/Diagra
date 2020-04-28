package com.diagra.model;

import java.util.List;

public class AlgorithmScheme {

    private final String name;
    private final List<Block> blocks;
    private final List<Edge> edges;

    public AlgorithmScheme(String name, List<Block> blocks, List<Edge> edges) {
        this.name = name;
        this.blocks = blocks;
        this.edges = edges;
    }

    public String getName() {
        return name;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<Edge> getEdges() {
        return edges;
    }

}
