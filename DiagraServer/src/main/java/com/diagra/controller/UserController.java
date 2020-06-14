package com.diagra.controller;

import com.diagra.dao.model.UserEntity;
import com.diagra.dto.UserDto;
import com.diagra.logic.algorithm.AlgorithmSchemeManager;
import com.diagra.mapper.UserMapper;
import com.diagra.utils.*;
import com.diagra.logic.user.UserManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserManager userManager;
    private final AlgorithmSchemeManager algorithmSchemeManager;
    private final UserMapper userMapper;

    public UserController(UserManager userManager, AlgorithmSchemeManager algorithmSchemeManager, UserMapper userMapper) {
        this.userManager = userManager;
        this.algorithmSchemeManager = algorithmSchemeManager;
        this.userMapper = userMapper;
    }

    @RequestMapping(
            method = {RequestMethod.DELETE}
    )
    public DeferredResult<ResponseEntity<Object>> delete(OAuth2Authentication user) {
        String userID = AuthUtil.getUserId(user);
        CustomDeferredResult<ResponseEntity<Object>> deferredResult = new CustomDeferredResult<>();
        algorithmSchemeManager.delete(userID).addCallback(new ListenableFutureCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                userManager.deleteUser(userID).addCallback(new DefaultResponseCallback<Void, Object>(deferredResult) {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deferredResult.setResult(ResponseEntity.ok().build());
                    }
                });
            }

            @Override
            public void onSuccess(Void aVoid) {
                userManager.deleteUser(userID).addCallback(new DefaultResponseCallback<Void, Object>(deferredResult) {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deferredResult.setResult(ResponseEntity.ok().build());
                    }
                });
            }
        });
        return deferredResult;
    }

    @RequestMapping(
            method = {RequestMethod.PUT},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<UserDto>> update(OAuth2Authentication user, @RequestBody @Valid @Validated(value = {PasswordValidationGroup.class}) UserDto userDto) {
        String userID = AuthUtil.getUserId(user);
        CustomDeferredResult<ResponseEntity<UserDto>> deferredResult = new CustomDeferredResult<>();
        UserEntity userEntity = userMapper.fromDto(userDto);
        userEntity.setId(userID);
        userManager.updateUser(userEntity, userDto.getNewPassword()).addCallback(new MappedResponseCallback<>(deferredResult, userMapper));
        return deferredResult;
    }

    @RequestMapping(
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<UserDto>> get(OAuth2Authentication user) {
        CustomDeferredResult<ResponseEntity<UserDto>> deferredResult = new CustomDeferredResult<>();
        userManager.loadById(AuthUtil.getUserId(user)).addCallback(new MappedResponseCallback<>(deferredResult, userMapper));
        return deferredResult;
    }

    @RequestMapping(
            method = {RequestMethod.POST},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<UserDto>> create(@RequestBody @Valid @Validated(value = {PasswordValidationGroup.class}) UserDto userDto) {
        CustomDeferredResult<ResponseEntity<UserDto>> deferredResult = new CustomDeferredResult<>();
        userManager.registerUser(userMapper.fromDto(userDto)).addCallback(new MappedResponseCallback<>(deferredResult, userMapper));
        return deferredResult;
    }

}
