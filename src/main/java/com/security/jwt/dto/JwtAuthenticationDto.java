package com.security.jwt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.security.jwt.utils.ApplicationType;

public class JwtAuthenticationDto {

	@Size(min = 2, max = 12, message="Username length must be between 2 and 12.")
	@NotBlank(message = "Username must not be null or blank.")
	private String userName;
	@NotBlank(message = "Password must not be null or blank.")
	@Size(min=4, max=12, message="Password length must be between 4 and 12.")
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
