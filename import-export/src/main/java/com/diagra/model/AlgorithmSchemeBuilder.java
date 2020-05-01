package com.diagra.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class AlgorithmSchemeBuilder {

    private static final List<String> TMP_CONNECTOR = ImmutableList.of("T", "M", "P", "D");

    private final String name;
    private final LinkedList<Block> blocks = new LinkedList<>();
    private final LinkedList<Edge> edges = new LinkedList<>();

    private final LinkedList<Block> decisions = new LinkedList<>();
    private final Map<Block, DecisionBlockInfo> decisionBlockInfo = new HashMap<>();

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
            lastPoints.add(lastPointInCurrentBlock);
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
        builder.blocks.addLast(new BaseBlock(BlockType.TERMINATOR, Collections.singletonList("Start")));
        return builder;
    }

    public String getName() {
        return name;
    }

    public void input(String input) {
        Block data = new BaseBlock(BlockType.DATA, Collections.singletonList(input));
        Block connect = peekForConnect();
        if (connect != null) {
            addEdge(new BaseEdge(EdgeType.LINE, null, connect, data));
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

    public void endDecision() {
        Block decision = decisions.removeLast();
        if (decision == null) {
            throw new IllegalStateException("Decision block already ended.");
        }
        DecisionBlockInfo info = decisionBlockInfo.get(decision);
        List<Block> list = info.getLastPoints();
        if (list.size() == 0) {
            this.blocks.remove(decision);
            edges.removeIf(edge -> edge.target() == decision);
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

    public AlgorithmScheme build() {
        if (!decisions.isEmpty()) {
            throw new IllegalStateException("Decision wasn't ended correctly.");
        }
        Block terminator = new BaseBlock(BlockType.TERMINATOR, Collections.singletonList("End"));
        this.addEdge(new BaseEdge(EdgeType.LINE, null, peekForConnect(), terminator));
        this.blocks.addLast(terminator);
        deleteInnerStructures();
        return new AlgorithmScheme(name, blocks, edges);
    }

    private void deleteInnerStructures() {
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
        if (decisions.isEmpty()) {
            edges.addLast(edge);
        } else if (decisionBlockInfo.get(decisions.peekLast()).getLastPointInCurrentBlock() == null) {
            Preconditions.checkState(decisions.peekLast() == edge.source());
            DecisionBlockInfo blockInfo = decisionBlockInfo.get(decisions.peekLast());
            for (Block block : blockInfo.populateCastedBlocks()) {
                edges.addLast(new BaseEdge(EdgeType.LINE, null, block, edge.target()));
            }
            Edge copy = new BaseEdge(edge.edgeType(), blockInfo.getLastBlockName(), edge.source(), edge.target());
            edges.addLast(copy);
            blockInfo.setLastPointInCurrentBlock(edge.target());
        } else {
            decisionBlockInfo.get(decisions.peekLast()).setLastPointInCurrentBlock(edge.target());
            edges.addLast(edge);
        }
    }

    private Block peekForConnect() {
        if (decisions.isEmpty()) {
            return blocks.peekLast();
        } else {
            Block block = decisions.peekLast();
            Block lastBlockInDecision = decisionBlockInfo.get(block).getLastPointInCurrentBlock();
            if (lastBlockInDecision == null) {
                return block;
            }
            return lastBlockInDecision;
        }
    }

}
