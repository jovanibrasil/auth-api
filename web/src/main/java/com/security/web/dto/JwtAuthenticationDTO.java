package com.security.web.dto;

import com.security.web.domain.ApplicationType;
import com.security.web.validators.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class JwtAuthenticationDTO {

	@Size(min = 2, max = 12, message="{error.user.name.size}")
	@NotBlank(message = "{error.user.name.notblank}")
	private String userName;
	@NotBlank(message = "{error.user.password.notblank}")
	@Size(min=4, max=12, message="{error.user.password.size}")
	private String password;
	@EnumValidator(enumClass=ApplicationType.class)
	private ApplicationType application;

	public JwtAuthenticationDTO(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

}