package com.diagra.auth;

import com.diagra.dao.model.UserEntity;
import com.diagra.dao.model.UserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

public class AuthUser extends User {

    public AuthUser(UserEntity userEntity) {
        super(userEntity.getUserName(), userEntity.getPassword(), getGrantedAuthorities(userEntity));
    }

    public static List<GrantedAuthority> getGrantedAuthorities(UserEntity userEntity) {
        List<GrantedAuthority> res = new ArrayList<>();
        for (UserRoles role : userEntity.getRoles()) {
            res.add(new SimpleGrantedAuthority(role.name().toLowerCase()));
        }
        return res;
    }

}
