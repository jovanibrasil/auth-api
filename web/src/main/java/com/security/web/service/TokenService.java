package com.security.web.service;

import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;

public interface TokenService {
    String createToken();
    String createToken(User currentUser, ApplicationType applicationType);
    String refreshToken(String token);
    User checkToken(String token);
}
