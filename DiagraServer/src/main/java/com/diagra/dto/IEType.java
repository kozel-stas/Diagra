package com.diagra.dto;

import com.diagra.ResourceType;

public enum IEType {

    JAVA_CODE(ResourceType.SOURCE_CODE_JAVA),
    XML(ResourceType.XML_MXGRAPH),
    ;

    private final ResourceType resourceType;

    IEType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
