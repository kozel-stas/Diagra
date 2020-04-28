package com.diagra.model;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class AlgorithmSchemeBuilder {

    private final String name;
    private final LinkedList<Block> blocks = new LinkedList<>();
    private final LinkedList<Edge> edges = new LinkedList<>();

    public AlgorithmSchemeBuilder(String name) {
        this.name = name;
    }

    public static AlgorithmSchemeBuilder builder(String name) {
        AlgorithmSchemeBuilder builder = new AlgorithmSchemeBuilder(name);
        builder.blocks.addLast(new BaseBlock(BlockType.TERMINATOR, Collections.singletonList("Start")));
        return builder;
    }

    public String getName() {
        return name;
    }

    public void input(String input) {
        Block last = blocks.peekLast();
        Block data;
        if (last == null || last.blockType() != BlockType.DATA) {
            data = new BaseBlock(BlockType.DATA, Collections.singletonList(input));
            if (peekForConnect() != null) {
                edges.addLast(new BaseEdge(EdgeType.LINE, null, peekForConnect(), data));
            }
        } else {
            last = blocks.removeLast();
            ListIterator<Edge> iterator = edges.listIterator();
            data = new BaseBlock(BlockType.DATA, ImmutableList.<String>builder().addAll(last.commands()).add(input).build());
            while (iterator.hasNext()) {
                Edge edge = iterator.next();
                if (edge.target().equals(last)) {
                    iterator.set(new BaseEdge(edge.edgeType(), edge.text(), edge.source(), data));
                }
            }
        }
        blocks.addLast(data);
    }

    public void process(String data) {
        Block last = blocks.peekLast();
        Block process;
        if (last == null || last.blockType() != BlockType.PROCESS) {
            process = new BaseBlock(BlockType.PROCESS, Collections.singletonList(data));
            if (peekForConnect() != null) {
                edges.addLast(new BaseEdge(EdgeType.LINE, null, peekForConnect(), process));
            }
        } else {
            last = blocks.removeLast();
            ListIterator<Edge> iterator = edges.listIterator();
            process = new BaseBlock(BlockType.PROCESS, ImmutableList.<String>builder().addAll(last.commands()).add(data).build());
            while (iterator.hasNext()) {
                Edge edge = iterator.next();
                if (edge.target().equals(last)) {
                    iterator.set(new BaseEdge(edge.edgeType(), edge.text(), edge.source(), process));
                }
            }
        }
        blocks.addLast(process);
    }

    public void method(String data) {
        Block method = new BaseBlock(BlockType.PREDEFINED_PROCESS, Collections.singletonList(data));
        Block connect = peekForConnect();
        if (connect != null) {
            edges.addLast(new BaseEdge(EdgeType.LINE, null, connect, method));
        }
        blocks.addLast(method);
    }

    public void decision(String blockData) {

    }

    public void endDecision() {

    }

    public void decisionBlock(String condition) {

    }

    public void endDecisionBlock() {

    }

    public AlgorithmScheme build() {
        Block terminator = new BaseBlock(BlockType.TERMINATOR, Collections.singletonList("End"));
        this.edges.addLast(new BaseEdge(EdgeType.LINE, null, peekForConnect(), terminator));
        this.blocks.addLast(terminator);
        return new AlgorithmScheme(name, blocks, edges);
    }

    private Block peekForConnect() {
        return blocks.peekLast();
    }

}
