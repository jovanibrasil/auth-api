package com.security.jwt.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.jwt.entities.User;
import com.security.jwt.security.utils.JwtUserFactory;
import com.security.jwt.services.UserService;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Searching for " + username);
		Optional<User> user = userService.findByUserName(username);
		if(user.isPresent()) {
			log.info("The user {} was found.", username);
			return JwtUserFactory.create(user.get());
		}
		log.error("No user {} was found.", username);
		throw new UsernameNotFoundException("User name not encontered.");
	}

}
