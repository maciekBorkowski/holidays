package com.mborkowski.holidays.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticationManager implements AuthenticationManager {
    @Value("${api.key}")
    private String apiKey = null;

    @Override
    public Authentication authenticate(Authentication authentication)  {
        if (!apiKey.equals(authentication.getPrincipal()))
            throw new BadCredentialsException("Given api key is invalid");
        authentication.setAuthenticated(true);
        return authentication;
    }
}
