package com.diagra.auth;

import com.diagra.dao.manager.BaseManager;
import com.diagra.dao.model.UserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("authUserDetailService")
public class AuthUserDetailService implements UserDetailsService {

    private final BaseManager<String, UserEntity> baseUserManager;

    public AuthUserDetailService(@Qualifier("baseUserManager") BaseManager<String, UserEntity> baseUserManager) {
        this.baseUserManager = baseUserManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = baseUserManager.get(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AuthUser(userEntity);
    }

}
