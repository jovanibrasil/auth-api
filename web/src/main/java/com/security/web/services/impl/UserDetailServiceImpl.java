package com.security.web.services.impl;

import com.security.jwt.utils.JwtUserFactory;
import com.security.web.domain.User;
import com.security.web.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Searching for " + username);
		User user = userService.findByUserName(username);
		return JwtUserFactory.create(user.getUserName(),
				user.getPassword(), user.getProfile());
	}

}
