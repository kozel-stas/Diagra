package com.diagra.dao.model;

public interface Edge {

    EdgeType edgeType();

    String text();

    Block source();

    Block target();

}
