package com.diagra.logic.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseServerException extends RuntimeException {

    public BaseServerException(String message) {
        super(message);
    }

    public BaseServerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ResponseEntity<String> exceptionRender() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
