package com.security.web.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.security.web.domain.User;

public interface UserService extends UserDetailsService {

	User findUserByUserName(String userName);
	User findUserByEmail(String email);
	User findUserById(Long id);
	
	User saveUser(User user);
	User updateUser(User user);
	void deleteUserByName(String userName);
	
}
