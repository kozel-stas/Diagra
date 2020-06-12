package com.diagra.xml;

import com.diagra.ConverterService;
import com.diagra.IEException;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.model.*;
import com.diagra.mxgraph.MxGraphModel;
import com.diagra.mxgraph.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MXGraphXmlConverter implements ConverterService {

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public ResourceType outputType() {
        return ResourceType.XML_MXGRAPH;
    }

    @Override
    public ResourceType inputType() {
        return ResourceType.INNER_REPRESENTATION;
    }

    @Override
    public Resource convert(Resource resource) throws IEException {
        AlgorithmScheme algorithmScheme = (AlgorithmScheme) resource.getObject();
        MxGraphModel mxGraphModel = objectFactory.createMxGraphModel();
        mxGraphModel.setRoot(objectFactory.createMxGraphModelRoot());
        int id = 0;
        for (int i = 0; i < 2; i++) {
            MxGraphModel.Root.MxCell cell = objectFactory.createMxGraphModelRootMxCell();
            cell.setId(id);
            if (i != 0) {
                cell.setParent(id - 1);
            }
            id++;
            mxGraphModel.getRoot().getMxCell().add(cell);
        }
        final Map<Block, Integer> map = new HashMap<>();
        for (Block block : algorithmScheme.getBlocks()) {
            MxGraphModel.Root.MxCell cell = objectFactory.createMxGraphModelRootMxCell();
            cell.setId(id);
            cell.setVertex((byte) 1);
            cell.setParent(1);
            cell.setValue(block.getText());
            MxGraphProperty property = MxGraphProperty.valueOf(block.blockType());
            if (property == null) {
                continue;
            }

            MxGraphModel.Root.MxCell.MxGeometry geo = objectFactory.createMxGraphModelRootMxCellMxGeometry();
            geo.setAs(property.getAs());
            geo.setHeight(property.getHeight());
            geo.setWidth(property.getWidth());
            cell.setStyle(property.getStyle());

            cell.setMxGeometry(geo);

            mxGraphModel.getRoot().getMxCell().add(cell);

            map.put(block, cell.getId());
            id++;
        }
        for (Edge edge : algorithmScheme.getEdges()) {
            MxGraphModel.Root.MxCell cell = objectFactory.createMxGraphModelRootMxCell();
            MxGraphModel.Root.MxCell.MxGeometry geo = objectFactory.createMxGraphModelRootMxCellMxGeometry();

            cell.setEdge((byte) 1);
            cell.setId(id);
            cell.setValue(edge.text());
            cell.setSource(map.get(edge.source()));
            cell.setTarget(map.get(edge.target()));
            cell.setParent(1);
            geo.setRelative((byte) 1);
            geo.setAs("geometry");
            cell.setMxGeometry(geo);
            if (cell.getSource() != null && cell.getTarget() != null) {
                mxGraphModel.getRoot().getMxCell().add(cell);
            }

            if (edge.edgeType() == EdgeType.DOTTED_LINE) {
                cell.setStyle("edgeStyle=elbowEdgeStyle;dashed=1;endArrow=none");
            } else {
                cell.setStyle("edgeStyle=elbowEdgeStyle");
            }

            id++;
        }
        return new Resource(ResourceType.XML_MXGRAPH, mxGraphModel);
    }

    private enum MxGraphProperty {

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
    }

}
