package com.example.config;

import com.example.BusinessException;
import com.example.service.UserDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(AbstractUserDetailsAuthenticationProvider.class);

    @NonNull
    private UserDetailsService userDetailsService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException, BusinessException {
        Object token = usernamePasswordAuthenticationToken.getCredentials();
        return Optional.ofNullable(token)
                .map(String::valueOf)
                .map(userDetailsService::getUserByToken)
                .orElseThrow(() -> new BusinessException(String.format("No authenticated user with token= %s", token)));
    }
}
