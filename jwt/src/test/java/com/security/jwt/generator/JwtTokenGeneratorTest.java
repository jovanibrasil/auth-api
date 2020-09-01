package com.security.jwt.generator;

import com.security.jwt.model.User;
import com.security.jwt.model.enums.ProfileEnum;
import com.security.jwt.util.JwtUser;
import com.security.jwt.util.JwtUserFactory;
import com.security.jwt.util.PasswordUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JwtTokenGeneratorTest {

	private User user;

	@Before
	public void setUp() throws Exception {
		user = new User();
		user.setUserName("teste");
		user.setEmail("teste@gmail.com");
		user.setPassword("123456");
	}

	@Test
	public void testCreateRegistrationToken() {
		System.setProperty("jwt.secret", "SECRET");
		System.setProperty("jwt.expiration", "60400");
		JwtTokenGenerator jwtTokenUtil = new JwtTokenGenerator("SECRET", 60400L);

		user.setPassword(PasswordUtils.generateHash(user.getPassword()));
		String token = jwtTokenUtil
				 .createRegistrationToken(user.getEmail(), "Teste");
		assertNotNull(token);
	}

	@Test
	public void testValidTokenChecking(){
		JwtTokenGenerator jwtTokenUtil = new JwtTokenGenerator("SECRET", 60L);
		String invalidToken = jwtTokenUtil.createToken(new JwtUser(user.getUserName(),
				user.getPassword(), JwtUserFactory.mapToGrantedAuthorities(ProfileEnum.ROLE_USER)), "BLOG_APP");
		assertTrue(jwtTokenUtil.tokenIsValid(invalidToken));
	}

	@Test
	public void testInvalidTokenChecking() {
		JwtTokenGenerator jwtTokenUtil = new JwtTokenGenerator("SECRET", 2L);
		String invalidToken = jwtTokenUtil.createToken(new JwtUser(user.getUserName(),
				user.getPassword(), JwtUserFactory.mapToGrantedAuthorities(ProfileEnum.ROLE_USER)), "BLOG_APP");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(jwtTokenUtil.tokenIsValid(invalidToken));
	}

}
