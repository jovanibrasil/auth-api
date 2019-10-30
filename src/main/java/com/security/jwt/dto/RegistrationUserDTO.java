package com.security.jwt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.security.validators.CustomNameValidator;

public class RegistrationUserDTO {

	@CustomNameValidator
	private String userName;
	
	@NotBlank(message = "Password must not be blank or null.")
	@Size(min=4, max=12, message="Password length must be between 4 and 12.")
	private String password;

	@NotBlank(message = "Token must not be blank or null.")
	private String token;
	
	public RegistrationUserDTO() {}
	
	public String getUserName() {
		return userName;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
