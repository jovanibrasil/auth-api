package com.jwt.security.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.validators.StringValidator;

public class UserDto {

	@NotNull
	@Email
	private String email;
	@NotNull
	@Size(min=2, max=10)
	private String userName;
	@NotNull
	@Size(min=4, max=10)
	private String password;
	
	@StringValidator(acceptedValues={"NOTES_APP", "BLOG_APP"}, message="Invalid dataType")
	private String application;
	
	public UserDto() {}

	public UserDto(String email, String userName, String password) {
		super();
		this.email = email;
		this.userName = userName;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String name) {
		this.userName = name;
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
