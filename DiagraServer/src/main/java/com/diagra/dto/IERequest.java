package com.diagra.dto;

import javax.validation.constraints.NotNull;

public class IERequest {

    @NotNull
    private IEType from;
    @NotNull
    private IEType to;
    @NotNull
    private String transitionLink;

    public void setFrom(IEType from) {
        this.from = from;
    }

    public void setTo(IEType to) {
        this.to = to;
    }

    public void setTransitionLink(String transitionLink) {
        this.transitionLink = transitionLink;
    }

    public IEType getFrom() {
        return from;
    }

    public String getTransitionLink() {
        return transitionLink;
    }

    public IEType getTo() {
        return to;
    }

}
