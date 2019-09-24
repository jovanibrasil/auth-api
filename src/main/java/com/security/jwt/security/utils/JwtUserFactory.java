package com.security.jwt.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;

public class JwtUserFactory {

	public JwtUserFactory() {}
	
	/*
	 * Create a JwtUser object based on User object information.
	 */
	public static JwtUser create(User user) {
		return new JwtUser(user.getUserName(), user.getPassword(), mapToGrantedAuthorities(user.getProfile()));
	}
	
	/*
	 * Convert a role to a lists of granted authorities.
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
		return authorities;
	}

}
