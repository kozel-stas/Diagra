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
            geo.setHeight(Double.valueOf(block.metaInfo().getOrDefault("height", String.valueOf(property.getHeight()))));
            geo.setWidth(Double.valueOf(block.metaInfo().getOrDefault("width", String.valueOf(property.getWidth()))));
            geo.setX(Double.valueOf(block.metaInfo().getOrDefault("x", String.valueOf(0))));
            geo.setY(Double.valueOf(block.metaInfo().getOrDefault("y", String.valueOf(0))));
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

}
