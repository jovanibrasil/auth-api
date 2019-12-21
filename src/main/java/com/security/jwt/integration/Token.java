package com.security.jwt.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.utils.ApplicationType;

@Slf4j
@Component
public class Token {

	private static String token = null;
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
