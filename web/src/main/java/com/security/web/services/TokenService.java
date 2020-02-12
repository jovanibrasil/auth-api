package com.security.web.services;

import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;

public interface TokenService {
    String getToken();
    String createToken(User currentUser, ApplicationType applicationType);
    String refreshToken(String token);
    User checkToken(String token);
}
