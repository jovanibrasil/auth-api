package com.security.jwt.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.security.jwt.entities.User;
import com.security.jwt.security.utils.JwtTokenUtil;

public class JwtTokenUtilTest {
	
	@Test
	public void testCreateRegistrationToken() {
		System.setProperty("jwt.secret", "SECRET");
		System.setProperty("jwt.expiration", "60400");
		JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("SECRET", 60400L);
		
		User u = new User();
		u.setUserName("teste");
		u.setEmail("teste@gmail.com");
		u.setPassword(PasswordUtils.generateHash("123456"));
		String token = jwtTokenUtil
				 .createRegistrationToken(u.getEmail(), ApplicationType.NOTES_APP);
		assertNotNull(token);
	}
	
	

}
