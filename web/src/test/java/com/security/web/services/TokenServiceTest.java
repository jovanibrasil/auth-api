package com.security.web.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.security.jwt.exception.TokenException;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.jwt.model.enums.ProfileEnum;
import com.security.jwt.util.JwtUser;
import com.security.jwt.util.JwtUserFactory;
import com.security.jwt.util.Utils;
import com.security.web.AuthenticationMock;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.service.UserService;
import com.security.web.service.impl.TokenServiceImpl;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    private static String token;
    @Mock
    private JwtTokenGenerator jwtTokenUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Utils utils;

    private JwtUser jwtUser;
    private User user;

    @Before
    public void setUp() throws TokenException {
        MockitoAnnotations.initMocks(this);
        tokenService = new TokenServiceImpl(jwtTokenUtil, userService, authenticationManager, utils);
        user = new User();
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        user.setProfile(ProfileEnum.ROLE_USER);

        Application app = new Application(ApplicationType.BLOG_APP);
        user.setRegistries(Arrays.asList(
                new Registry(app, user)
        ));

        jwtUser = new JwtUser(user.getUsername(),
                user.getPassword(),
                JwtUserFactory.mapToGrantedAuthorities(user.getProfile()));
    }

    @Test
    public void refreshToken() {
        when(utils.extractJwtTokenFromBearerHeader("Bearer token")).thenReturn("token");
        when(jwtTokenUtil.refreshToken("token")).thenReturn("newToken");

        assertEquals("newToken", tokenService.refreshToken("Bearer token"));
    }

    @Test(expected = TokenException.class)
    public void refreshNullToken() {
        when(utils.extractJwtTokenFromBearerHeader(null)).thenThrow(TokenException.class);
        tokenService.refreshToken(null);
    }

    @Test(expected = TokenException.class)
    public void refreshBlankToken() {
        when(utils.extractJwtTokenFromBearerHeader("")).thenThrow(TokenException.class);
        tokenService.refreshToken("");
    }

    @Test(expected = TokenException.class)
    public void refreshInvalidToken() {
        token = "pOa";
        when(utils.extractJwtTokenFromBearerHeader(token)).thenThrow(TokenException.class);
        tokenService.refreshToken(token);
    }

    @Test
    public void checkValidToken() {
        token = "token";
        when(utils.extractJwtTokenFromBearerHeader(token)).thenReturn(token);
        when(jwtTokenUtil.getUserNameFromToken(token)).thenReturn(user.getUsername());
        when(jwtTokenUtil.getApplicationName(token)).thenReturn("BLOG_APP");
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        
        assertEquals(user, tokenService.checkToken(token));
    }

    @Test
    public void createToken() {
        token = "token";
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(jwtUser);
        when(authenticationManager.authenticate(any())).thenReturn(new AuthenticationMock());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(jwtUser);
        when(jwtTokenUtil.createToken(jwtUser, ApplicationType.BLOG_APP)).thenReturn(token);
        
        assertEquals("token", tokenService.createToken(user, ApplicationType.BLOG_APP));
    }

}