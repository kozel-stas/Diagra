package com.diagra.utils;


import com.diagra.auth.AuthUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;

public class AuthUtil {

    private AuthUtil() {
    }

    public static String getUserId(Authentication principal) {
        String userId = null;
        if (principal.getDetails() instanceof OAuth2AuthenticationDetails && ((OAuth2AuthenticationDetails) principal.getDetails()).getDecodedDetails() instanceof Map) {
            userId = ((Map) ((OAuth2AuthenticationDetails) principal.getDetails()).getDecodedDetails()).get(AuthUser.USER_ID_KEY).toString();
        }
        if (userId == null) {
            throw new BadCredentialsException("Token isn't valid.");
        }
        return userId;
    }

}
