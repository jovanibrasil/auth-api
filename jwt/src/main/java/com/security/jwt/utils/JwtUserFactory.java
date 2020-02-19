package com.security.jwt.utils;

import com.security.jwt.enums.ProfileEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class JwtUserFactory {

	public JwtUserFactory() {}
	
	/*
	 * Create a JwtUser object based on User object information.
	 */
	public static JwtUser create(String userName, String password, ProfileEnum profileEnum) {
		return new JwtUser(userName, password, mapToGrantedAuthorities(profileEnum));
	}
	
	/*
	 * Convert a role to a lists of granted authorities.
	 */
	public static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
		return authorities;
	}

}
