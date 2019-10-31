package com.security.jwt.dto;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.CustomEmailValidator;
import com.security.validators.EnumValidator;

public class ConfirmUserDTO {
	
	@CustomEmailValidator
	private String email;
	@EnumValidator(enumClass=ApplicationType.class)
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
