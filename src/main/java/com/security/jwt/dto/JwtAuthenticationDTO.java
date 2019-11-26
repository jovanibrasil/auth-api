package com.security.jwt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.lang.NonNull;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.EnumValidator;

public class JwtAuthenticationDTO {

	@Size(min = 2, max = 12, message="Username length must be between 2 and 12.")
	@NotBlank(message = "Username must not be blank or null.")
	private String userName;
	@NotBlank(message = "Password must not be blank or null.")
	@Size(min=4, max=12, message="Password length must be between 4 and 12.")
	private String password;
	@EnumValidator(enumClass=ApplicationType.class)
	private ApplicationType application;
	
	public JwtAuthenticationDTO() {}
	
	public JwtAuthenticationDTO(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	public JwtAuthenticationDTO(String userName, String password, ApplicationType application) {
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