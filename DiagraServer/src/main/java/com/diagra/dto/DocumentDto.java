package com.diagra.dto;

import com.diagra.utils.IDValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;

public class DocumentDto {

    @NotNull(groups = IDValidationGroup.class)
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String transitionLink;
    @NotNull
    private IEType type;
    @JsonIgnore
    private String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTransitionLink(String transitionLink) {
        this.transitionLink = transitionLink;
    }

    public void setType(IEType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getTransitionLink() {
        return transitionLink;
    }

    public String getName() {
        return name;
    }

    public IEType getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

}
