package com.security.jwt.services;

import com.security.jwt.entities.User;
import com.security.jwt.exceptions.implementations.UserServiceException;

import java.util.Optional;

public interface UserService {

	Optional<User> findByUserName(String userName);
	User save(User user) throws UserServiceException;
	Optional<User> findUserByEmail(String email);
	void deleteUser(String userName) throws UserServiceException;
	User updateUser(User user) throws UserServiceException;
	Optional<User> findUserById(Long id);
	
}
