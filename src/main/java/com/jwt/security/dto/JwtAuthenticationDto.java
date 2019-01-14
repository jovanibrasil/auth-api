package com.jwt.security.dto;

import javax.validation.constraints.NotNull;

public class JwtAuthenticationDto {

	@NotNull
	private String userName;

	@NotNull
	private String password;
	
	public JwtAuthenticationDto() {}
	
	public JwtAuthenticationDto(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
