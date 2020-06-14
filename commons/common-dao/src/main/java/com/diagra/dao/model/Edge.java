package com.diagra.dao.model;


public interface Edge extends MetaInfo {

    EdgeType edgeType();

    String text();

    Block source();

    Block target();

}
