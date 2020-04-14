package com.diagra.dao.manager;

public interface BaseManager<K, V> {

    V get(K k);

}
