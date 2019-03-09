package com.jwt.security.dto;

import javax.validation.constraints.NotNull;

import com.validators.StringValidator;

public class JwtAuthenticationDto {

	@NotNull
	private String userName;

	@NotNull
	private String password;
	
	@StringValidator(acceptedValues={"NOTES_APP", "BLOG_APP"}, message="Invalid dataType")
	private String application;
	
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
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	
}
