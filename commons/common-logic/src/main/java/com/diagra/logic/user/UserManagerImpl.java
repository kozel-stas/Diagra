package com.diagra.logic.user;

import com.diagra.dao.manager.BaseManagerImpl;
import com.diagra.dao.model.UserEntity;
import com.diagra.logic.exceptions.UserDuplicateException;
import com.diagra.logic.exceptions.UserNotFoundException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Service
public class UserManagerImpl extends BaseManagerImpl<String, UserEntity> implements UserManager {

    private final AsyncListenableTaskExecutor asyncListenableTaskExecutor;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserManagerImpl(MongoRepository<UserEntity, String> repository, Function<String, Example<UserEntity>> matcher, AsyncListenableTaskExecutor asyncListenableTaskExecutor) {
        super(repository, matcher);
        this.asyncListenableTaskExecutor = asyncListenableTaskExecutor;
    }

    @Override
    public ListenableFuture<UserEntity> loadById(String id) {
        return asyncListenableTaskExecutor.submitListenable(() -> repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public ListenableFuture<UserEntity> loadByUserName(String userName) {
        return asyncListenableTaskExecutor.submitListenable(() -> {
            UserEntity userEntity = get(userName);
            if (userEntity == null) {
                throw new UserNotFoundException(userName);
            }
            return userEntity;
        });
    }

    @Override
    public ListenableFuture<UserEntity> loginUser(String id) {
        return loadById(id);
    }

    @Override
    public ListenableFuture<UserEntity> registerUser(UserEntity userEntity) {
        return asyncListenableTaskExecutor.submitListenable(() -> {
            try {
                userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                return repository.insert(userEntity);
            } catch (DuplicateKeyException e) {
                throw new UserDuplicateException(userEntity, e);
            } catch (Exception e) {
                throw e;
            }
        });
    }

    @Override
    public ListenableFuture<Collection<UserEntity>> getUsers() {
        return asyncListenableTaskExecutor.submitListenable((Callable<Collection<UserEntity>>) repository::findAll);
    }

    @Override
    public ListenableFuture<Void> deleteUser(String userId) {
        return asyncListenableTaskExecutor.submitListenable(() -> {
            repository.deleteById(userId);
            return null;
        });
    }

}
