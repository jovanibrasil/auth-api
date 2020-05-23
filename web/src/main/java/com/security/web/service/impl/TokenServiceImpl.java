package com.security.web.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.jwt.util.Utils;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.web.exception.implementation.ForbiddenUserException;
import com.security.web.exception.implementation.NotFoundException;
import com.security.web.exception.implementation.UnauthorizedUserException;
import com.security.web.exception.implementation.ValidationException;
import com.security.web.service.TokenService;
import com.security.web.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

	private static String token = null;
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final Utils utils;

	public TokenServiceImpl(JwtTokenGenerator jwtTokenUtil, UserService userService, 
			AuthenticationManager authenticationManager, Utils utils) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.utils = utils;
	}

	@Override
	public String createToken() {
		log.info("Generating Service token.");
		if(token == null) {
			try {
				UserDetails userDetails = userService.loadUserByUsername("AUTH");
				log.info("Creating the token ...");
				token = jwtTokenUtil.createToken(userDetails, ApplicationType.AUTH_APP);
				log.info("Generated token: {}.", token);
			} catch (Exception e) {
				e.printStackTrace(); // todo handle this exception
			}	
		}
		return token;
	}

	@Override
	public String createToken(User currentUser, ApplicationType applicationType) {
		log.info("User {} is requesting a JWT token.", currentUser.getUsername());

		// Verify if user has registry for the required application
		try {
			User savedUser = userService.findUserByUserName(currentUser.getUsername());
			if(!savedUser.hasRegistry(applicationType)) {
				log.error("Authentication error. User not register for {}", applicationType);
				throw new ForbiddenUserException("error.user.notregistered");
			}
		} catch (NotFoundException nfe){
			throw new UnauthorizedUserException("error.login.invalid");
		}

		// Does user authentication
		log.info("Authenticating {} ...", currentUser.getUsername());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(currentUser.getUsername(), currentUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);

		// Verify authentication result
		if(!auth.isAuthenticated()) {
			log.error("Authentication error {}");
			throw new UnauthorizedUserException("error.login.invalid");
		}
		log.info("Creating token for {}", currentUser.getUsername());
		UserDetails userDetails = userService.loadUserByUsername(currentUser.getUsername());
		String token = jwtTokenUtil.createToken(userDetails, applicationType);
		log.info("Token successfully generated for {}.", currentUser.getUsername());
		return token;
	}

	@Override
	public String refreshToken(String token) {
		token = utils.extractJwtTokenFromBearerHeader(token);
		return jwtTokenUtil.refreshToken(token);
	}

	@Override
	public User checkToken(String token) {
		token = utils.extractJwtTokenFromBearerHeader(token);

		String userName = jwtTokenUtil.getUserNameFromToken(token);
		ApplicationType applicationName = ApplicationType.valueOf(jwtTokenUtil.getApplicationName(token));
		User user = userService.findUserByUserName(userName);

		if(user.hasRegistry(applicationName)) {
			log.info("Token ok from user {}", userName);
			return user;
		}else{
			log.error("Token checking error. {} not registered for application {}.", userName, applicationName);
		}
		
		throw new ValidationException("error.user.notregistered");
	}

}
