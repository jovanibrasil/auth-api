package com.security.web.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class UpdateUserForm {

	@NotNull
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String actualPassword;

	@NotNull
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String newPassword;

	public UpdateUserForm(String actualPassword, String newPassword) {
		this.actualPassword = actualPassword;
		this.newPassword = newPassword;
	}
	
}
