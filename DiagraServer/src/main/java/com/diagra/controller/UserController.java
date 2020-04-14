package com.diagra.controller;

import com.diagra.dto.UserDto;
import com.diagra.mapper.UserMapper;
import com.diagra.utils.*;
import com.diagra.logic.user.UserManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
    private final UserMapper userMapper;

    public UserController(UserManager userManager, UserMapper userMapper) {
        this.userManager = userManager;
        this.userMapper = userMapper;
    }

    @RequestMapping(
            method = {RequestMethod.DELETE}
    )
    public DeferredResult<ResponseEntity<?>> delete(OAuth2Authentication user) {
        CustomDeferredResult<ResponseEntity<?>> deferredResult = new CustomDeferredResult<>();
        userManager.deleteUser(AuthUtil.getUserId(user)).addCallback(new DefaultResponseCallback<Void>(deferredResult) {
            @Override
            public void onSuccess(Void aVoid) {
                deferredResult.setResult(ResponseEntity.ok().build());
            }
        });
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
