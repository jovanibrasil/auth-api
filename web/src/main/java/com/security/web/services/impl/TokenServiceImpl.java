package com.security.web.services.impl;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.jwt.utils.Utils;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.NotFoundException;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import com.security.web.exceptions.implementations.ValidationException;
import com.security.web.services.TokenService;
import com.security.web.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

	private static String token = null;
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserDetailsService userDetailsService;
	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final Utils utils;

	public TokenServiceImpl(JwtTokenGenerator jwtTokenUtil,
							@Lazy @Qualifier("userDetailServiceImpl") UserDetailsService userDetailsService,
							UserService userService, AuthenticationManager authenticationManager, Utils utils) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.utils = utils;
	}

	@Override
	public String getToken() {
		log.info("Generating Service token.");
		if(token == null) {
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername("AUTH");
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
		log.info("User {} is requesting a JWT token.", currentUser.getUserName());
		//String recaptchaResponse = request.getParameter("recaptchaResponseToken");
		//captchaService.processResponse(recaptchaResponse);

		// Verify if user has registry for the required application
		try {
			User savedUser = userService.findByUserName(currentUser.getUserName());
			if(!savedUser.hasRegistry(applicationType)) {
				log.error("Authentication error. User not register for {}", applicationType);
				throw new ForbiddenUserException("error.user.notregistered");
			}
		} catch (NotFoundException nfe){
			throw new UnauthorizedUserException("error.login.invalid");
		}

		// Does user authentication
		log.info("Authenticating {} ...", currentUser.getUserName());
		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(currentUser.getUserName(), currentUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);

		// Verify authentication result
		if(!auth.isAuthenticated()) {
			log.error("Authentication error {}");
			throw new UnauthorizedUserException("error.login.invalid");
		}
		log.info("Creating token for {}", currentUser.getUserName());
		UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getUserName());
		String token = jwtTokenUtil.createToken(userDetails, applicationType);
		log.info("Token successfully generated for {}.", currentUser.getUserName());
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
		User user = userService.findByUserName(userName);

		if(user.hasRegistry(applicationName)) {
			log.info("Token ok from user {}", userName);
			return user;
		}else{
			log.error("Token checking error. {} not registered for application {}.", userName, applicationName);
		}
		
		throw new ValidationException("error.user.notregistered");
	}

}
