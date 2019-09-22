package com.jwt.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jwt.security.entities.User;
import com.jwt.security.utils.JwtTokenUtil;

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
				 .createRegistrationToken(u, ApplicationType.NOTES_APP);
		assertNotNull(token);
	}
	
	@Test
	public void testGetUserFromToken() {
		
		JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("SECRET", 60400L);
		
		
		User u = new User();
		u.setUserName("teste");
		u.setEmail("teste@gmail.com");
		u.setPassword("123456");
		
		String token = jwtTokenUtil
				 .createRegistrationToken(u, ApplicationType.NOTES_APP);
		
		User tokenUser = jwtTokenUtil.getUserFromToken(token);
		
		assertEquals(u.getUserName(), tokenUser.getUserName());
		assertEquals(u.getEmail(), tokenUser.getEmail());
		assertTrue((PasswordUtils.verifyPassword(u.getPassword(),
				tokenUser.getPassword())));
		assertEquals(
				"NOTES_APP", 
				tokenUser.getRegistries().get(0).getApplication().getApplication().name());
	}

}
