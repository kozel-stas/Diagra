package com.diagra;

public class Resource {

    private final ResourceType resourceType;
    private final Object object;

    public Resource(ResourceType resourceType, Object object) {
        this.resourceType = resourceType;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

}

