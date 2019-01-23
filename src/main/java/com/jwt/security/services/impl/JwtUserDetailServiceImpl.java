package com.jwt.security.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwt.security.entities.User;
import com.jwt.security.services.UserService;
import com.jwt.security.utils.JwtUserFactory;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("Procurando por "+username);
		Optional<User> user = userService.getUserByName(username);
		if(user.isPresent())
			return JwtUserFactory.create(user.get());
		throw new UsernameNotFoundException("User name not encontered");
	}

}
