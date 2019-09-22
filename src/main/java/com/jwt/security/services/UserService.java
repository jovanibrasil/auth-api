package com.jwt.security.services;

import java.util.Optional;

import com.jwt.exceptions.UserServiceException;
import com.jwt.security.entities.User;

public interface UserService {

	Optional<User> findByUserName(String userName);
	User save(User user) throws UserServiceException;
	Optional<User> findUserByEmail(String email);
	void deleteUser(String userName) throws UserServiceException;
	User updateUser(User user) throws UserServiceException;
	Optional<User> findUserById(Long id);
	
}
