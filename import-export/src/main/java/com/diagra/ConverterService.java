package com.diagra;

public interface ConverterService {

    ResourceType outputType();

    ResourceType inputType();

    Resource convert(Resource resource) throws IEException;

}
