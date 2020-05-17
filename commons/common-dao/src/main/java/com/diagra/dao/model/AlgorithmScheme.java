package com.diagra.dao.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;

@Document(collection = "algorithms")
public class AlgorithmScheme {

    @Id
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    @Field
    private String name;
    @Field
    @Indexed
    private String ownerID;
    @Field
    private String description;
    @Field
    private List<Block> blocks = new ArrayList<>();
    @Field
    private List<Edge> edges = new ArrayList<>();
    @Field
    @Indexed
    private Map<String, AccessType> accessTypes = new HashMap<>();

    public AlgorithmScheme() {
    }

    @PersistenceConstructor
    public AlgorithmScheme(
            String id,
            String name,
            List<Block> blocks,
            List<Edge> edges,
            String ownerID,
            Map<String, AccessType> accessTypes,
            String description
    ) {
        setDescription(description);
        setId(id);
        setName(name);
        setBlocks(blocks);
        setEdges(edges);
        setOwnerID(ownerID);
        setAccessTypes(accessTypes);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public Map<String, AccessType> getAccessTypes() {
        return accessTypes;
    }

    public String getId() {
        return id;
    }

    public void setAccessTypes(Map<String, AccessType> accessTypes) {
        this.accessTypes = new HashMap<>(accessTypes);
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public void setEdges(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

}
