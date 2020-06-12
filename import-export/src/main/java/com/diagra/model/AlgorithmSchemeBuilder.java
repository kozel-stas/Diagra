package com.diagra.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AlgorithmSchemeBuilder {

    private static final List<String> TMP_CONNECTOR = ImmutableList.of("T", "M", "P", "D");
    private static final List<String> START_TERMINATOR = Collections.singletonList("Start");
    private static final List<String> END_TERMINATOR = Collections.singletonList("Stop");

    protected final String name;
    protected final LinkedList<Block> blocks = new LinkedList<>();
    protected final LinkedList<Edge> edges = new LinkedList<>();
    protected final Block start = new BaseBlock(BlockType.TERMINATOR, START_TERMINATOR);
    protected final Block end = new BaseBlock(BlockType.TERMINATOR, END_TERMINATOR);

    protected final LinkedList<Block> decisions = new LinkedList<>();
    private final Map<Block, DecisionBlockInfo> decisionBlockInfo = new HashMap<>();

    private final LinkedList<Block> cycles = new LinkedList<>();
    private final Map<Block, Block> pointAfterCycle = new HashMap<>();

    private final class DecisionBlockInfo {

        private Block lastPointInCurrentBlock;
        private String lastBlockName;
        private final List<Block> lastPoints = new LinkedList<>();

        private final List<Block> castedBlock = new LinkedList<>();

        public void setLastBlockName(String lastBlockName) {
            this.lastBlockName = lastBlockName;
        }

        public void setLastPointInCurrentBlock(Block lastPointInCurrentBlock) {
            this.lastPointInCurrentBlock = lastPointInCurrentBlock;
        }

        private void castBlock() {
            emulateBlock();
            castedBlock.add(lastPointInCurrentBlock);
            lastPointInCurrentBlock = null;
            lastBlockName = null;
        }

        public void endBlock() {
            emulateBlock();
            boolean gotoConnects = lastPointInCurrentBlock == end || pointAfterCycle.containsKey(lastPointInCurrentBlock) || pointAfterCycle.containsValue(lastPointInCurrentBlock);
            if (!gotoConnects) {
                lastPoints.add(lastPointInCurrentBlock);
            }
            lastPointInCurrentBlock = null;
            lastBlockName = null;
        }

        private void emulateBlock() {
            if (lastPointInCurrentBlock == null) {
                Block emulate = new BaseBlock(BlockType.CONNECTOR, TMP_CONNECTOR);
                Block connect = peekForConnect();
                if (connect != null) {
                    addEdge(new BaseEdge(EdgeType.LINE, lastBlockName, connect, emulate));
                }
                blocks.addLast(emulate);
            }
        }

        public List<Block> populateCastedBlocks() {
            List<Block> res = ImmutableList.copyOf(castedBlock);
            castedBlock.clear();
            return res;
        }

        public Block getLastPointInCurrentBlock() {
            return lastPointInCurrentBlock;
        }

        public String getLastBlockName() {
            return lastBlockName;
        }

        public List<Block> getLastPoints() {
            return Collections.unmodifiableList(lastPoints);
        }

    }

    public AlgorithmSchemeBuilder(String name) {
        this.name = name;
    }

    public static AlgorithmSchemeBuilder builder(String name) {
        AlgorithmSchemeBuilder builder = new AlgorithmSchemeBuilder(name);
        builder.blocks.addLast(builder.start);
        return builder;
    }

    public static AlgorithmSchemeBuilder builder(@Nullable AlgorithmScheme main, AlgorithmScheme... sub) {
        if (main == null) {
            List<Block> blocks = new ArrayList<>();
            List<Edge> edges = new ArrayList<>();
            String name = "";
            for (AlgorithmScheme value : sub) {
                blocks.addAll(value.getBlocks());
                edges.addAll(value.getEdges());
                name = value.getName();
            }
            AlgorithmSchemeBuilder algorithmSchemeBuilder = new AlgorithmSchemeBuilder(name) {
                @Override
                public AlgorithmScheme build() {
                    if (!this.decisions.isEmpty()) {
                        throw new IllegalStateException("Decision wasn't ended correctly.");
                    }
                    deleteInnerStructures();
                    return new AlgorithmScheme(name, this.blocks, edges);
                }
            };
            algorithmSchemeBuilder.blocks.addAll(blocks);
            algorithmSchemeBuilder.edges.addAll(edges);
            return algorithmSchemeBuilder;
        } else {
            AlgorithmSchemeBuilder builder = new AlgorithmSchemeBuilder(main.getName());
            Map<String, AlgorithmScheme> methods = Arrays.stream(sub).collect(Collectors.toMap(AlgorithmScheme::getName, v -> v));
            for (AlgorithmScheme o : ImmutableList.<AlgorithmScheme>builder().add(main).addAll(Arrays.asList(sub)).build()) {
                List<Block> blocks = new LinkedList<>();
                List<Edge> edges = new LinkedList<>(o.getEdges());
                for (Block block : o.getBlocks()) {
                    boolean function = block.blockType() == BlockType.PREDEFINED_PROCESS && block.commands().size() == 1 && methods.get(block.commands().get(0)) != null;
                    if (function) {
                        AlgorithmScheme algorithmScheme = methods.get(o.getName());
                        for (Edge edge : algorithmScheme.getEdges()) {
                            if (edge.source().blockType() == BlockType.TERMINATOR && START_TERMINATOR.equals(edge.source().commands())) {
//                                edges.add(new BaseEdge(EdgeType.LINE, edge.text(), ));
                            }
                            if (edge.source().blockType() == BlockType.TERMINATOR && END_TERMINATOR.equals(edge.source().commands())) {

                            }
                        }
                        continue;
                    }
//                    if (o != main && block.blockType() == BlockType.TERMINATOR) {
//                        edges.removeIf(edge -> edge.target() == block || edge.source() == block);
//                        continue;
//                    }
                    blocks.add(block);
                }
                builder.blocks.addAll(blocks);
                builder.edges.addAll(edges);
            }
            return builder;
        }
    }

    public String getName() {
        return name;
    }

    public void data(String input) {
        Block data = new BaseBlock(BlockType.DATA, Collections.singletonList(input));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, data));
        }
        blocks.addLast(data);
    }

    public void comment(String comment) {
        Block data = new BaseBlock(BlockType.COMMENT, Collections.singletonList(comment));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.DOTTED_LINE, null, connect, data));
        }
        blocks.addLast(data);
    }

    public void process(String data) {
        Block process = new BaseBlock(BlockType.PROCESS, Collections.singletonList(data));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, process));
        }
        blocks.addLast(process);
    }

    public void method(String data) {
        Block method = new BaseBlock(BlockType.PREDEFINED_PROCESS, Collections.singletonList(data));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, method));
        }
        blocks.addLast(method);
    }

    public void decision(String blockData) {
        Block decision = new BaseBlock(BlockType.DECISION, Collections.singletonList(blockData));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, decision));
        }
        decisions.addLast(decision);
        decisionBlockInfo.put(decision, new DecisionBlockInfo());
        blocks.addLast(decision);
    }

    public void terminate() {
        Block connect = peekForConnect();
        if (connect == null) {
            throw new IllegalStateException("No block for connect.");
        }
        addEdge(new BaseEdge(EdgeType.LINE, null, connect, end));
    }

    public void endDecision() {
        Block decision = decisions.removeLast();
        if (decision == null) {
            throw new IllegalStateException("Decision block already ended.");
        }
        DecisionBlockInfo info = decisionBlockInfo.get(decision);
        List<Block> list = info.getLastPoints();
        if (list.size() == 0) {
//            this.blocks.remove(decision);
//            edges.removeIf(edge -> edge.target() == decision); //FIXME
            return;
        }
        Block tempConnector = new BaseBlock(BlockType.CONNECTOR, TMP_CONNECTOR); // Should be removed on build level.
        for (Block block : list) {
            addEdge(new BaseEdge(EdgeType.LINE, null, block, tempConnector));
        }
        blocks.addLast(tempConnector);
    }

    public void decisionBlock(String condition) {
        Block decision = decisions.peekLast();
        if (decision == null) {
            throw new IllegalStateException("Decision block absent.");
        }
        decisionBlockInfo.get(decision).setLastBlockName(condition);
    }

    public void endDecisionBlock() {
        Block decision = decisions.peekLast();
        if (decision == null) {
            throw new IllegalStateException("Decision block absent.");
        }
        decisionBlockInfo.get(decision).endBlock();
    }

    public void endDecisionBlockCast() {
        Block decision = decisions.peekLast();
        if (decision == null) {
            throw new IllegalStateException("Decision block absent.");
        }
        decisionBlockInfo.get(decision).castBlock();
    }

    public void startCycle(String condition) {
        Block cycle = new BaseBlock(BlockType.CYCLE_START, Collections.singletonList(condition));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, cycle));
        }
        this.pointAfterCycle.put(cycle, new BaseBlock(BlockType.CONNECTOR, TMP_CONNECTOR));
        this.cycles.addLast(cycle);
        this.blocks.addLast(cycle);
    }

    public void endCycle() {
        Block cycle = this.cycles.pollLast();
        if (cycle == null) {
            throw new IllegalStateException("Cycle block already ended.");
        }
        Block pointAfter = this.pointAfterCycle.remove(cycle);
        Block cycleEnd = new BaseBlock(BlockType.CYCLE_END, cycle.commands());
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, cycleEnd));
            addEdge(new BaseEdge(EdgeType.LINE, null, cycleEnd, pointAfter));
        }
        this.blocks.addLast(cycleEnd);
        this.blocks.addLast(pointAfter);
    }

    public void toStartCycle() {
        Block cycle = this.cycles.peekLast();
        if (cycle == null) {
            throw new IllegalStateException("Cycle block already ended.");
        }
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, cycle));
        }
    }

    public void exitCycle() {
        Block cycle = this.cycles.peekLast();
        if (cycle == null) {
            throw new IllegalStateException("Cycle block already ended.");
        }
        Block afterPoint = this.pointAfterCycle.get(cycle);
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, afterPoint));
        }
    }

    public AlgorithmScheme build() {
        if (!decisions.isEmpty()) {
            throw new IllegalStateException("Decision wasn't ended correctly.");
        }
        this.addEdgeIfNotExists(new BaseEdge(EdgeType.LINE, null, peekForConnect(), end));
        this.blocks.addLast(end);
        deleteInnerStructures();
        return new AlgorithmScheme(name, blocks, edges);
    }

    protected void deleteInnerStructures() {
        Iterator<Block> blockIterator = blocks.iterator();
        //FIXME: Extra iterations
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (block.blockType() == BlockType.CONNECTOR && block.commands() == TMP_CONNECTOR) {
                List<Edge> in = new LinkedList<>();
                Edge out = null;
                for (Edge edge : edges) {
                    if (edge.source() == block) {
                        out = edge;
                    }
                    if (edge.target() == block) {
                        in.add(edge);
                    }
                }
                if (in.size() > 0 && out != null) {
                    this.edges.removeAll(in);
                    this.edges.remove(out);
                    blockIterator.remove();
                    for (Edge edge : in) {
                        addEdge(new BaseEdge(edge.edgeType(), edge.text(), edge.source(), out.target()));
                    }
                }
            }
        }
    }

    private void addEdge(Edge edge) {
        if (decisions.isEmpty() || edge.target().blockType() == BlockType.COMMENT) {
            edges.addLast(edge);
        } else if (decisionBlockInfo.get(decisions.peekLast()).getLastPointInCurrentBlock() == null) {
            Preconditions.checkState(decisions.peekLast() == edge.source());
            DecisionBlockInfo blockInfo = decisionBlockInfo.get(decisions.peekLast());
            for (Block block : blockInfo.populateCastedBlocks()) {
                edges.addLast(new BaseEdge(EdgeType.LINE, null, block, edge.target()));
            }
            Edge copy = new BaseEdge(edge.edgeType(), blockInfo.getLastBlockName(), edge.source(), edge.target());
            blockInfo.setLastPointInCurrentBlock(edge.target());
            edges.addLast(copy);
        } else {
            decisionBlockInfo.get(decisions.peekLast()).setLastPointInCurrentBlock(edge.target());
            edges.addLast(edge);
        }
    }

    protected void addEdgeIfNotExists(Edge edge) {
        if (edges
                .stream()
                .filter(value -> value.source() == edge.source())
                .filter(value -> value.edgeType() == edge.edgeType())
                .filter(value -> Objects.equals(value.text(), edge.text()))
                .noneMatch(value -> value.target() == edge.target())
        ) {
            edges.addLast(edge);
        }
    }

    private Block peekForConnect() {
        if (decisions.isEmpty()) {
            Iterator<Block> descendingIterator = blocks.descendingIterator();
            while (descendingIterator.hasNext()) {
                Block block = descendingIterator.next();
                if (block.blockType() != BlockType.COMMENT) {
                    return block;
                }
            }
        } else {
            Block block = decisions.peekLast();
            Block lastBlockInDecision = decisionBlockInfo.get(block).getLastPointInCurrentBlock();
            if (lastBlockInDecision == null) {
                return block;
            }
            return lastBlockInDecision;
        }
        return start;
    }

}
