package com.jwt.security.services;

import java.util.Optional;

import com.jwt.security.entities.User;

public interface UserService {

	/*
	 * Get user by email.
	 */
	Optional<User> getUserByName(String userName);
	
}
