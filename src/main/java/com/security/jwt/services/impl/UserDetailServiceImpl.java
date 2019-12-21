package com.security.jwt.services.impl;

import com.security.jwt.entities.User;
import com.security.jwt.security.utils.JwtUserFactory;
import com.security.jwt.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

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
