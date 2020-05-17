package com.diagra.utils;

import com.diagra.logic.exceptions.BaseServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

public abstract class DefaultResponseCallback<T> implements ListenableFutureCallback<T> {

    protected final CustomDeferredResult<ResponseEntity<?>> customDeferredResult;

    public DefaultResponseCallback(CustomDeferredResult<ResponseEntity<?>> customDeferredResult) {
        this.customDeferredResult = customDeferredResult;
    }

    @Override
    public void onFailure(Throwable throwable) {
        if (throwable instanceof BaseServerException) {
            customDeferredResult.setErrorResult(((BaseServerException) throwable).exceptionRender());
            return;
        }
        customDeferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}
