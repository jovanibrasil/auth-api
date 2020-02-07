package com.security.web.services.impl;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.domain.ApplicationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenServiceImpl {

	private static String token = null;
	private final JwtTokenGenerator jwtTokenUtil;
	private final UserDetailsService userDetailsService;

	public TokenServiceImpl(JwtTokenGenerator jwtTokenUtil,
							@Qualifier("userDetailServiceImpl") @Lazy UserDetailsService userDetailsService) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
	}

	public String getToken() {
		log.info("Generating Service token.");
		if(token == null) {
			try {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername("AUTH");
				log.info("Creating the token ...");
				token = jwtTokenUtil.createToken(userDetails, ApplicationType.AUTH_APP);
				log.info("Generated token: {}.", token);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		return token;
	}
	
}
