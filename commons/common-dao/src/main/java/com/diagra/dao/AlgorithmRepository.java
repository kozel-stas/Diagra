package com.diagra.dao;

import com.diagra.dao.model.AlgorithmScheme;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlgorithmRepository extends MongoRepository<AlgorithmScheme, String> {

    List<AlgorithmScheme> findAlgorithmSchemeByOwnerID(String ownerID);

    void deleteAlgorithmSchemeByOwnerID(String ownerID);

}

