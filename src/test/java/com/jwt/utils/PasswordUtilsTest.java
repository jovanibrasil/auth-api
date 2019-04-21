package com.jwt.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PasswordUtilsTest {

	/*
 	 * Test an invalid password on hash generation function.
 	 */
 	@Test
 	public void generateHashNullRawPassword() {
 		assertEquals(null, PasswordUtils.generateHash(null));
 	}
	
 	/*
 	 * Test valid has generation using a valid password.
 	 */
 	@Test
 	public void generateHashValidPassword() {
 		String hash = PasswordUtils.generateHash("TEST");
 		assertNotNull(hash);
 	}
	
 	/*
 	 * Valid match test using valid hash and valid password. 
 	 */
 	@Test
 	public void verifyPasswordValid() {
 		String hash = PasswordUtils.generateHash("TEST");
 		assertEquals(true, PasswordUtils.verifyPassword("TEST", hash));
 	}
	
 	/*
 	 * Invalid match test using invalid hash and valid password. 
 	 */
 	@Test
 	public void verifyPasswordWithInvalidHash() {
 		assertEquals(false, PasswordUtils.verifyPassword("TEST", "INVALID_HASH"));
 	}
	
 	/*
 	 * Invalid match test using valid hash and invalid password. 
 	 */
 	@Test
 	public void verifyPasswordWithInvalidPassword() {
 		assertEquals(false, PasswordUtils.verifyPassword("INVALID_PASSWORD", "TEST"));
 	}
	
 	/*
 	 * Invalid match test using valid hash and invalid password (null password). 
 	 */
 	@Test
 	public void verifyPasswordWithNullPassword() {
 		assertEquals(false, PasswordUtils.verifyPassword(null, "TEST"));
 	}
	
}
