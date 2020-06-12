package com.diagra.logic.user;

import com.diagra.dao.manager.BaseManager;
import com.diagra.dao.model.UserEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;

public interface UserManager extends BaseManager<String, UserEntity> {

    ListenableFuture<UserEntity> loadById(String id);

    ListenableFuture<UserEntity> loadByUserName(String userName);

    ListenableFuture<UserEntity> loginUser(String id);

    ListenableFuture<UserEntity> updateUser(UserEntity userEntity);

    ListenableFuture<UserEntity> registerUser(UserEntity userEntity);

    ListenableFuture<Collection<UserEntity>> getUsers();

    ListenableFuture<Void> deleteUser(String userId);

}
