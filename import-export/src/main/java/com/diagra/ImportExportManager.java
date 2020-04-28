package com.diagra;

public interface ImportExportManager {

    Resource convert(Resource resource, ResourceType targetType) throws IEException;

}
