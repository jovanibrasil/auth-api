package com.security.jwt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.security.validators.CustomNameValidator;

public class RegistrationUserDTO {

	@CustomNameValidator
	private String userName;
	
	@NotBlank(message = "{error.user.password.notblank}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;

	@NotBlank(message = "{error.token.notblank}")
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
