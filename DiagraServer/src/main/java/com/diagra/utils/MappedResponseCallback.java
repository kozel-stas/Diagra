package com.diagra.utils;

import com.diagra.logic.exceptions.BaseServerException;
import com.diagra.mapper.EntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class MappedResponseCallback<K, T> implements ListenableFutureCallback<T> {

    private final CustomDeferredResult<ResponseEntity<K>> customDeferredResult;
    private final EntityMapper<K, T> mapper;

    public MappedResponseCallback(CustomDeferredResult<ResponseEntity<K>> customDeferredResult, EntityMapper<K, T> mapper) {
        this.customDeferredResult = customDeferredResult;
        this.mapper = mapper;
    }

    @Override
    public void onSuccess(T t) {
        customDeferredResult.setResult(ResponseEntity.ok(mapper.toDto(t)));
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
