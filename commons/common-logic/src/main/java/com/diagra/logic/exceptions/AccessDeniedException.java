package com.diagra.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccessDeniedException extends BaseServerException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public ResponseEntity<String> exceptionRender() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
