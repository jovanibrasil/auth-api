package com.security.jwt.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.CustomEmailValidator;
import com.security.validators.CustomNameValidator;
import com.security.validators.EnumValidator;

public class CreateUserDTO {

	private Long id;

	@CustomEmailValidator
	private String email;
	@CustomNameValidator
	private String userName;
	@NotNull(message = "{error.user.password.notnull}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;
	@EnumValidator(enumClass=ApplicationType.class)
	private ApplicationType application;
	
	public CreateUserDTO() {}

	public CreateUserDTO(Long id, String email, String userName, String password) {
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
