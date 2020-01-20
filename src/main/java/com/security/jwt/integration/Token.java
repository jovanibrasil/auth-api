package com.security.jwt.integration;

import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.utils.ApplicationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Token {

	private static String token = null;
	private JwtTokenUtil jwtTokenUtil;
	private UserDetailsService userDetailsService;

	public Token(JwtTokenUtil jwtTokenUtil, @Qualifier("userDetailServiceImpl") @Lazy UserDetailsService userDetailsService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
	}

	public String getToken() {
		if(token == null) {
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername("AUTH");
				log.info("Creating a token ...");
				token = jwtTokenUtil.createToken(userDetails, ApplicationType.AUTH_APP);
				log.info("Generated token: " + token);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		return token;
	}
	
}
