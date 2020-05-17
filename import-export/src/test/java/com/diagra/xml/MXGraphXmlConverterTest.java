package com.diagra.xml;

import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.model.AlgorithmScheme;
import com.diagra.model.AlgorithmSchemeBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MXGraphXmlConverterTest {

    private static final String RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<mxGraphModel>\n" +
            "    <root>\n" +
            "        <mxCell id=\"0\"/>\n" +
            "        <mxCell id=\"1\" parent=\"0\"/>\n" +
            "        <mxCell id=\"2\" parent=\"1\" style=\"shape=data;\" vertex=\"1\" value=\"int a = 1\">\n" +
            "            <mxGeometry width=\"100\" height=\"60\" as=\"geometry\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"3\" parent=\"1\" style=\"shape=comment\" vertex=\"1\" value=\"test comment\">\n" +
            "            <mxGeometry width=\"100\" height=\"100\" as=\"geometry\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"4\" parent=\"1\" style=\"\" vertex=\"1\" value=\"any();\">\n" +
            "            <mxGeometry width=\"100\" height=\"40\" as=\"geometry\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"5\" parent=\"1\" style=\"shape=ellipse\" vertex=\"1\" value=\"End\">\n" +
            "            <mxGeometry width=\"100\" height=\"40\" as=\"geometry\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"7\" parent=\"1\" style=\"edgeStyle=elbowEdgeStyle;dashed=1;endArrow=none\" edge=\"1\" source=\"2\" target=\"3\">\n" +
            "            <mxGeometry as=\"geometry\" relative=\"1\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"8\" parent=\"1\" style=\"edgeStyle=elbowEdgeStyle\" edge=\"1\" source=\"2\" target=\"4\">\n" +
            "            <mxGeometry as=\"geometry\" relative=\"1\"/>\n" +
            "        </mxCell>\n" +
            "        <mxCell id=\"9\" parent=\"1\" style=\"edgeStyle=elbowEdgeStyle\" edge=\"1\" source=\"4\" target=\"5\">\n" +
            "            <mxGeometry as=\"geometry\" relative=\"1\"/>\n" +
            "        </mxCell>\n" +
            "    </root>\n" +
            "</mxGraphModel>\n";

    private final MXGraphXmlConverter mxGraphXmlConverter = new MXGraphXmlConverter();

    @Test
    public void testConvertToXml() throws IOException {
        Resource resource = new Resource(
                ResourceType.INNER_REPRESENTATION,
                algorithmScheme()
        );

        Resource xml = mxGraphXmlConverter.convert(resource);

        Assert.assertEquals(
                ResourceType.XML_MXGRAPH,
                xml.getResourceType()
        );
        Assert.assertEquals(
                RESULT_XML,
                IOUtils.toString(
                        ResourceType.XML_MXGRAPH.toResource(
                                xml.getObject()
                        ).getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
    }

    private static AlgorithmScheme algorithmScheme() {
        AlgorithmSchemeBuilder algorithmSchemeBuilder = new AlgorithmSchemeBuilder("test");
        algorithmSchemeBuilder.data("int a = 1");
        algorithmSchemeBuilder.comment("test comment");
        algorithmSchemeBuilder.process("any();");
        return algorithmSchemeBuilder.build();
    }

}