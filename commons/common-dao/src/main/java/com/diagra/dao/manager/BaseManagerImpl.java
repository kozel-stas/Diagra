package com.diagra.dao.manager;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.function.Function;

public class BaseManagerImpl<K, V> implements BaseManager<K, V> {

    protected final MongoRepository<V, K> repository;
    protected final Function<K, Example<V>> matcher;

    public BaseManagerImpl(MongoRepository<V, K> repository, Function<K, Example<V>> matcher) {
        this.repository = repository;
        this.matcher = matcher;
    }

    @Override
    public V get(K k) {
        return repository.findOne(matcher.apply(k)).orElse(null);
    }

}
