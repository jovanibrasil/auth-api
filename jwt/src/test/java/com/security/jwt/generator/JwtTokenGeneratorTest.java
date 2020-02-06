package com.security.jwt.generator;

import com.security.jwt.model.User;
import com.security.jwt.utils.PasswordUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JwtTokenGeneratorTest {
	
	@Test
	public void testCreateRegistrationToken() {
		System.setProperty("jwt.secret", "SECRET");
		System.setProperty("jwt.expiration", "60400");
		JwtTokenGenerator jwtTokenUtil = new JwtTokenGenerator("SECRET", 60400L);
		
		User u = new User();
		u.setUserName("teste");
		u.setEmail("teste@gmail.com");
		u.setPassword(PasswordUtils.generateHash("123456"));
		String token = jwtTokenUtil
				 .createRegistrationToken(u.getEmail(), "Teste");
		assertNotNull(token);
	}
	
	

}
