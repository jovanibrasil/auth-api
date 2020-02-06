package com.security.web.dto;

import com.security.web.domain.ApplicationType;
import com.security.web.validators.CustomEmailValidator;
import com.security.web.validators.CustomNameValidator;
import com.security.web.validators.EnumValidator;
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
