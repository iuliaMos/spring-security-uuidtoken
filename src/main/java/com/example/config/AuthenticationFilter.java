package com.example.config;

import com.example.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = LoggerFactory.getLogger(AbstractAuthenticationProcessingFilter.class);

    private static final String BEARER = "Bearer";

    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthenticationFilter(final RequestMatcher requestMatcher,
                                final HandlerExceptionResolver handlerExceptionResolver) {
        super(requestMatcher);
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

        try {
            String authHeader = Optional.ofNullable(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).orElse("");
            String token = Optional.of(authHeader)
                    .map(value -> StringUtils.removeStart(value, BEARER).trim())
                    .orElseThrow(() -> new BusinessException("Bad credentials, missing auth token"));

            final Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
            return getAuthenticationManager().authenticate(auth);
        } catch (BusinessException e) {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
