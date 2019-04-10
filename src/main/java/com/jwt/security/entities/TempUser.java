package com.jwt.security.entities;

import com.jwt.security.enums.ProfileEnum;

public class TempUser {

	private String name;
	private ProfileEnum role;
	
	public TempUser() {}
	
	public TempUser(String name, ProfileEnum role) {
		this.name = name;
		this.role = role;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProfileEnum getRole() {
		return role;
	}
	public void setRole(ProfileEnum role) {
		this.role = role;
	}
	
}
