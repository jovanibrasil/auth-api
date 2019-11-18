package com.security.jwt.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateUserDTO {

	@NotNull
	@Size(min=4, max=12, message="Password length must be between 4 and 12.")
	private String actualPassword;

	@NotNull
	@Size(min=4, max=12, message="Password length must be between 4 and 12.")
	private String newPassword;
	
	public UpdateUserDTO() {}

	public UpdateUserDTO(String actualPassword, String newPassword) {
		super();
		this.actualPassword = actualPassword;
		this.newPassword = newPassword;
	}

	public String getActualPassword() {
		return actualPassword;
	}

	public void setActualPassword(String actualPassword) {
		this.actualPassword = actualPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		return "UpdateUserDTO [actualPassword=" + actualPassword + ", newPassword=" + newPassword + "]";
	}	
	
}
