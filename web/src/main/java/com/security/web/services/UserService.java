package com.security.web.services;

import com.security.web.domain.User;
import com.security.web.exceptions.implementations.UserServiceException;

import java.util.List;
import java.util.Optional;

public interface UserService {

	Optional<User> findByUserName(String userName);
	User save(User user) throws UserServiceException;
	Optional<User> findUserByEmail(String email);
	void deleteUser(String userName) throws UserServiceException;
	User updateUser(User user) throws UserServiceException;
	Optional<User> findUserById(Long id);
	List<String> validateUser(User user);

}
