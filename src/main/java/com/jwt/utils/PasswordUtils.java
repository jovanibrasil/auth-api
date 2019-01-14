package com.jwt.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

	/*
	 * Generate a hash using BCrypt library.
	 * 
	 * @Param rawPassword			raw password
	 * @return String 				encoded password (hash)
	 * 
	 */
	public static String generateHash(String rawPassword) {
		if(rawPassword == null)
			return rawPassword;
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
		return bCryptEncoder.encode(rawPassword);
	}
	
	/*
	 * Verify if a raw password matches with an encoded password.
	 * 
	 * @Param rawPassword			raw password
	 * @Param encodedPassword		encoded password (hash)
	 * 
	 * @return Boolean
	 * 
	 */
	public static boolean verifyPassword(String rawPassword, String encodedPassword) {
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
		return bCryptEncoder.matches(rawPassword, encodedPassword);
	}
	
}
