package com.diagra.logic.algorithm;

import com.diagra.dao.model.AlgorithmScheme;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

public interface AlgorithmSchemeManager {

    ListenableFuture<List<AlgorithmScheme>> get(String userID);

    ListenableFuture<AlgorithmScheme> store(AlgorithmScheme algorithmScheme);

    ListenableFuture<AlgorithmScheme> update(AlgorithmScheme algorithmScheme);

    ListenableFuture<Void> delete(String id, String userID);

    ListenableFuture<Void> delete(String userID);

}
