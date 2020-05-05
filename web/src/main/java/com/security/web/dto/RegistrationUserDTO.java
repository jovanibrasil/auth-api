package com.security.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RegistrationUserDTO {

	@NotBlank(message = "{error.user.name.notblank}")
	private String userName;
	
	@NotBlank(message = "{error.user.password.notblank}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;

	@NotBlank(message = "{error.token.notblank}")
	private String token;

}
