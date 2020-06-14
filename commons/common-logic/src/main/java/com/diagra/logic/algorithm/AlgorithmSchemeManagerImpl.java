package com.diagra.logic.algorithm;

import com.diagra.dao.AlgorithmRepository;
import com.diagra.dao.model.AlgorithmScheme;
import com.diagra.logic.exceptions.AccessDeniedException;
import com.diagra.logic.exceptions.AlgorithmSchemeNotFoundException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

@Service
public class AlgorithmSchemeManagerImpl implements AlgorithmSchemeManager {

    private final AlgorithmRepository algorithmRepository;
    private final AsyncListenableTaskExecutor taskExecutor;

    public AlgorithmSchemeManagerImpl(AlgorithmRepository algorithmRepository, AsyncListenableTaskExecutor taskExecutor) {
        this.algorithmRepository = algorithmRepository;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public ListenableFuture<List<AlgorithmScheme>> get(String userID) {
        return taskExecutor.submitListenable(() -> algorithmRepository.findAlgorithmSchemeByOwnerID(userID));
    }


    public ListenableFuture<AlgorithmScheme> get(String userID, String id) {
        return taskExecutor.submitListenable(() -> {
            AlgorithmScheme algorithmScheme = algorithmRepository.findById(id).orElse(null);
            if (algorithmScheme == null || !algorithmScheme.getOwnerID().equals(userID)) {
                throw new AccessDeniedException("Not found.");
            }
            return algorithmScheme;
        });
    }

    @Override
    public ListenableFuture<AlgorithmScheme> store(AlgorithmScheme algorithmScheme) {
        return taskExecutor.submitListenable(() -> algorithmRepository.insert(algorithmScheme));
    }

    @Override
    public ListenableFuture<AlgorithmScheme> update(AlgorithmScheme algorithmScheme) {
        return taskExecutor.submitListenable(() -> {
            AlgorithmScheme stored = algorithmRepository.findById(algorithmScheme.getId()).orElse(null);
            if (stored == null) {
                throw new AlgorithmSchemeNotFoundException(algorithmScheme.getId());
            }
            if (!stored.getOwnerID().equals(algorithmScheme.getOwnerID())) {
                throw new AccessDeniedException("Owners are different.");
            }
            return algorithmRepository.save(algorithmScheme);
        });
    }

    @Override
    public ListenableFuture<Void> delete(String id, String userId) {
        return taskExecutor.submitListenable(() -> {
            AlgorithmScheme stored = algorithmRepository.findById(id).orElse(null);
            if (stored == null) {
                throw new AlgorithmSchemeNotFoundException(id);
            }
            if (!stored.getOwnerID().equals(userId)) {
                throw new AccessDeniedException("Owners are different.");
            }
            algorithmRepository.deleteById(id);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> delete(String userID) {
        return taskExecutor.submitListenable(() -> {
            algorithmRepository.deleteAlgorithmSchemeByOwnerID(userID);
            return null;
        });
    }

}
