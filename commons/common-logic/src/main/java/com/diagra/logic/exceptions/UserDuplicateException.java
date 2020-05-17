package com.diagra.logic.exceptions;

import com.diagra.dao.model.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserDuplicateException extends BaseServerException {

    public UserDuplicateException(UserEntity userEntity, Throwable e) {
        super(String.format("User %s with email %s can't be registered.", userEntity.getUserName(), userEntity.getEmail()), e);
    }

    @Override
    public ResponseEntity<String> exceptionRender() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
