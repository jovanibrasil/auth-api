package com.security.jwt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.EnumValidator;

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
