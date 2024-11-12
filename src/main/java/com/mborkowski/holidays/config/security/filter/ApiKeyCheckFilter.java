package com.mborkowski.holidays.config.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyCheckFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final String API_KEY_HEADER = "x-api-key";

    public ApiKeyCheckFilter(AuthenticationManager authManager){
        setAuthenticationManager(authManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(API_KEY_HEADER);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }


}
