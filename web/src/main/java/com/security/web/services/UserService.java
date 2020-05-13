package com.security.web.services;

import com.security.web.domain.User;

public interface UserService {

	User findByUserName(String userName);
	User findByEmail(String email);
	User findById(Long id);
	
	User save(User user);
	User update(User user);
	void deleteByName(String userName);
	
}
