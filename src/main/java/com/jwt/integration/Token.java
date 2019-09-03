package com.jwt.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.jwt.security.utils.JwtTokenUtil;
import com.jwt.utils.ApplicationType;

@Component
public class Token {

	private static String token = null;
	
	private final Logger log = LoggerFactory.getLogger(Token.class);
	
	private JwtTokenUtil jwtTokenUtil;
	private UserDetailsService userDetailsService;
	
	@Autowired
	public Token(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
	}
	
	public String getToken() {
		if(token == null) {
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername("AUTH");
				log.info("Creating a token ...");
				token = jwtTokenUtil.createToken(userDetails, ApplicationType.AUTH_APP); //AUTH_APP
				log.info("Generated token: " + token);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		return token;
	}
	
}
