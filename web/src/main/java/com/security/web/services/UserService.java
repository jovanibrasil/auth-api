package com.security.web.services;

import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;

import java.util.List;

public interface UserService {

	User findByUserName(String userName);
	boolean existUserWithUserName(String userName);
	User saveUser(User user);
	User findUserByEmail(String email);
	boolean existUserWithEmail(String email);
	void deleteUserByName(String userName);
	User updateUser(User user);
	User findUserById(Long id);
	List<String> validateUser(User user);
    void confirmUserEmail(User user, ApplicationType applicationType);
	boolean authenticate(String userName, String password);
}
