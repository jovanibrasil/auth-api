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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Searching for " + username);
		Optional<User> user = userService.findByUserName(username);
		if(user.isPresent()) {
			log.info("The user {} was found.", username);
			return JwtUserFactory.create(user.get().getUserName(),
					user.get().getEmail(), user.get().getProfile());
		}
		log.error("No user {} was found.", username);
		throw new UsernameNotFoundException("User name not encontered.");
	}

}
