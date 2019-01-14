package com.jwt.security.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwt.security.entities.User;
import com.jwt.security.repositories.UserRepository;
import com.jwt.security.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Optional<User> getUserByName(String userName) {
		return Optional.ofNullable(this.userRepository.findByUserName(userName));
	}
	
}
