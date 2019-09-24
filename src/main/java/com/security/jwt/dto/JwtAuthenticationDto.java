package com.security.jwt.dto;

import javax.validation.constraints.NotNull;

import com.security.jwt.utils.ApplicationType;

public class JwtAuthenticationDto {

	@NotNull
	private String userName;
	@NotNull
	private String password;
	private ApplicationType application;
	
	public JwtAuthenticationDto() {}
	
	public JwtAuthenticationDto(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	public JwtAuthenticationDto(@NotNull String userName, @NotNull String password, ApplicationType application) {
		super();
		this.userName = userName;
		this.password = password;
		this.application = application;
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
	public ApplicationType getApplication() {
		return application;
	}
	public void setApplication(ApplicationType application) {
		this.application = application;
	}
	
}
