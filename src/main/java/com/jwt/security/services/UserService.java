package com.jwt.security.services;

import java.util.Optional;

import com.jwt.security.entities.User;
import com.jwt.security.services.impl.UserServiceException;

public interface UserService {

	/*
	 * Get user by email.
	 */
	Optional<User> findByUserName(String userName);
	void save(User user) throws UserServiceException;
	Optional<User> findUserByEmail(String email);
	void deleteUser(String userName) throws UserServiceException;
	User updateUser(User user) throws UserServiceException;
	Optional<User> findUserById(Long id);
	
}
