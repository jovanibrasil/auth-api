package com.security.jwt.enums;

import org.springframework.security.core.GrantedAuthority;

/*
 * Define the users profile types.
 */
public enum ProfileEnum implements GrantedAuthority  {
	ROLE_ADMIN, ROLE_USER, ROLE_SERVICE;

	@Override
	public String getAuthority() {
		return name();
	}
}
