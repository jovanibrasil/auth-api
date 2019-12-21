package com.security.jwt.dto;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.CustomEmailValidator;
import com.security.validators.CustomNameValidator;
import com.security.validators.EnumValidator;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString
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

}
