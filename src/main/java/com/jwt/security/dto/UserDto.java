package com.jwt.security.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.jwt.utils.ApplicationType;
import com.validators.UserEmailValidator;
import com.validators.UserNameValidator;

public class UserDto {

	private Long id;

	@UserEmailValidator
	private String email;
	@UserNameValidator
	private String userName;
	@NotNull
	@Size(min=4, max=10, message="Password length must be between 4 and 10.")
	private String password;
	@NotNull
	//@EnumListValidator(enumClass=ApplicationType.class)
	private ApplicationType application;
	
	public UserDto() {}

	public UserDto(Long id, String email, String userName, String password) {
		super();
		this.id = id;
		this.email = email;
		this.userName = userName;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public ApplicationType getApplication() {
		return application;
	}

	public void setApplication(ApplicationType application) {
		this.application = application;
	}

	@Override
	public String toString() {
		return "UserDto [id=" + id + ", email=" + email + ", userName=" + userName + ", password=" + password
				+ ", application=" + application + "]";
	}
	
}
