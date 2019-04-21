package com.jwt.security.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.jwt.utils.ApplicationType;
import com.validators.EnumListValidator;

public class JwtAuthenticationDto {

	@NotNull
	private String userName;

	@NotNull
	private String password;
	
	//@EnumListValidator(enumClass=ApplicationType.class)
	private ApplicationType application;
	
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
	public ApplicationType getApplication() {
		return application;
	}
	public void setApplication(ApplicationType application) {
		this.application = application;
	}
	
}
