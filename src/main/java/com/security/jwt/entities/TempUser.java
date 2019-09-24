package com.security.jwt.entities;

import com.security.jwt.enums.ProfileEnum;

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

	@Override
	public String toString() {
		return "TempUser [name=" + name + ", role=" + role + "]";
	}
	
}
