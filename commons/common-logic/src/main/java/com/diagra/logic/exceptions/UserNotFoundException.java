package com.diagra.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserNotFoundException extends BaseServerException {

    public UserNotFoundException(String message) {
        super("User " + message + "isn't found.");
    }

    @Override
    public ResponseEntity<String> exceptionRender() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
