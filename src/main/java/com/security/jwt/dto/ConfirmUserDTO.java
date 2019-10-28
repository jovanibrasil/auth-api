package com.security.jwt.dto;

import javax.validation.constraints.NotNull;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.CustomEmailValidator;

public class ConfirmUserDTO {
	
	@CustomEmailValidator
	private String email;
	
	@NotNull
	//@EnumListValidator(enumClass=ApplicationType.class)
	private ApplicationType application;
	
	public ConfirmUserDTO() {}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ApplicationType getApplication() {
		return application;
	}

	public void setApplication(ApplicationType application) {
		this.application = application;
	}

}
