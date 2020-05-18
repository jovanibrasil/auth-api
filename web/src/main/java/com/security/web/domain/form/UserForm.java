package com.security.web.domain.form;

import com.security.web.domain.ApplicationType;
import com.security.web.validators.EnumValidator;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UserForm {

	@NotBlank(message = "{error.email.notnull}")
	@Email(message = "{error.email.invalid}")
	private String email;
	@NotBlank(message = "{error.user.name.notblank}")
	@Size(message = "{error.user.name.size}", min = 2, max = 12)
	private String userName;
	@NotBlank(message = "{error.user.password.notblank}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;
	@EnumValidator(enumClass=ApplicationType.class)
	private ApplicationType application;

}
