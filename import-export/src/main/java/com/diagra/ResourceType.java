package com.diagra;

import com.diagra.model.AlgorithmScheme;
import com.diagra.mxgraph.MxGraphModel;
import org.apache.commons.io.IOUtils;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

public enum ResourceType {

    SOURCE_CODE_JAVA(String.class, MediaType.TEXT_PLAIN) {
        @Override
        public Object fromResource(Resource resource) throws IOException {
            return IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
        }

        @Override
        public Resource toResource(Object resource) {
            return new InputStreamResource(IOUtils.toInputStream(resource.toString(), Charset.defaultCharset()));
        }

    },
    INNER_REPRESENTATION(AlgorithmScheme.class, null),
    XML_MXGRAPH(String.class, MediaType.TEXT_XML) {
        Marshaller jaxbMarshaller;
        {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(MxGraphModel.class);
                jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            } catch (JAXBException e) {
                throw new IllegalStateException("Application initialization failed. JAXB init failed.");
            }
        }

        @Override
        public Object fromResource(Resource resource) throws IOException {
            return IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
        }

        @Override
        public Resource toResource(Object resource) throws IllegalArgumentException {
            try {
                StringWriter sw = new StringWriter();
                jaxbMarshaller.marshal(resource, sw);
                return new InputStreamResource(IOUtils.toInputStream(sw.toString(), Charset.defaultCharset()));
            } catch (JAXBException e) {
                throw new IllegalArgumentException("Object doesnt correspond with JAXB.", e);
            }
        }

    },
    ;

    private final Class clazz;
    private final MediaType mediaType;

    ResourceType(Class clazz, MediaType mediaType) {
        this.clazz = clazz;
        this.mediaType = mediaType;
    }

    public Object fromResource(Resource resource) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Resource toResource(Object resource) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public MediaType getMediaType() {
        return mediaType;
    }

}
