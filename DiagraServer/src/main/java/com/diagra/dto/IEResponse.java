package com.diagra.dto;

public class IEResponse {

    private String transitionLink;
    private IEType format;

    public void setTransitionLink(String transitionLink) {
        this.transitionLink = transitionLink;
    }

    public void setFormat(IEType format) {
        this.format = format;
    }

    public IEType getFormat() {
        return format;
    }

    public String getTransitionLink() {
        return transitionLink;
    }

}
