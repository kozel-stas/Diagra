package com.diagra.xml;

import com.diagra.model.BlockType;

public enum MxGraphProperty {

    DECISION(100, 100, "geometry", "shape=rhombus", BlockType.DECISION),
    PROCESS(100, 40, "geometry", "", BlockType.PROCESS),
    PREDEFINED_PROCESS(100, 60, "geometry", "shape=predefined_process", BlockType.PREDEFINED_PROCESS),
    CONNECTOR(40, 40, "geometry", "shape=ellipse", BlockType.CONNECTOR),
    DATA(100, 60, "geometry", "shape=data;", BlockType.DATA),
    TERMINATOR(100, 25, "geometry", "shape=terminator", BlockType.TERMINATOR),
    CYCLE_START(100, 50, "geometry", "shape=cycle_start", BlockType.CYCLE_START),
    CYCLE_END(100, 50, "geometry", "shape=cycle_end", BlockType.CYCLE_END),
    COMMENT(100, 100, "geometry", "shape=comment", BlockType.COMMENT),
    ;

    private final int width;
    private final int height;
    private final String as;
    private final String style;
    private final BlockType blockType;

    MxGraphProperty(int width, int height, String as, String style, BlockType blockType) {
        this.width = width;
        this.height = height;
        this.as = as;
        this.style = style;
        this.blockType = blockType;
    }

    public int getHeight() {
        return height;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public int getWidth() {
        return width;
    }

    public String getAs() {
        return as;
    }

    public String getStyle() {
        return style;
    }

    public static MxGraphProperty valueOf(BlockType blockType) {
        for (MxGraphProperty value : values()) {
            if (value.blockType == blockType) {
                return value;
            }
        }
        return null;
    }

    public static MxGraphProperty valueOfByStyle(String style) {
        if ("".equals(style)) {
            return MxGraphProperty.PROCESS;
        }
        for (MxGraphProperty value : values()) {
            if (value.style.equals(style)) {
                return value;
            }
        }
        return null;
    }
}
