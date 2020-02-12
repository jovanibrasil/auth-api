package com.security.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationMock implements Authentication {

    private static final long serialVersionUID = 6373211614082998724L;

    private boolean isAuthenticated;

    public AuthenticationMock() {
        this.isAuthenticated = true;
    }

    public AuthenticationMock(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() { return null; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return null; }

    @Override
    public boolean isAuthenticated() { return isAuthenticated; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
}


