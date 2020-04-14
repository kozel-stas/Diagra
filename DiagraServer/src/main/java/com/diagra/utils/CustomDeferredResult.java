package com.diagra.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.TimeUnit;

public class CustomDeferredResult<T> extends DeferredResult<T> {

    public CustomDeferredResult() {
        super(TimeUnit.SECONDS.toMillis(30));
        errorHandler();
    }

    public CustomDeferredResult(Long timeoutValue) {
        super(timeoutValue);
        errorHandler();
    }

    private void errorHandler() {
        onTimeout(() -> {
            this.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timeout occurred."));
        });
        onError((e) -> {
            this.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error."));
        });
    }

}
