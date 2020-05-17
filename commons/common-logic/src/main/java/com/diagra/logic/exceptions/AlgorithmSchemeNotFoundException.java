package com.diagra.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AlgorithmSchemeNotFoundException extends BaseServerException {

    public AlgorithmSchemeNotFoundException(String message) {
        super("Algorithm " + message + "isn't found.");
    }

    @Override
    public ResponseEntity<String> exceptionRender() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
