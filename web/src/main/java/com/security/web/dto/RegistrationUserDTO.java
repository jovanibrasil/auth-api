package com.security.web.dto;

import com.security.web.validators.CustomNameValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class RegistrationUserDTO {

	@CustomNameValidator
	private String userName;
	
	@NotBlank(message = "{error.user.password.notblank}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;

	@NotBlank(message = "{error.token.notblank}")
	private String token;

}
