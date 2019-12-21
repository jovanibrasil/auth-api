package com.security.jwt.dto;

import com.security.jwt.utils.ApplicationType;
import com.security.validators.CustomEmailValidator;
import com.security.validators.EnumValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ConfirmUserDTO {
	
	@CustomEmailValidator
	private String email;
	@EnumValidator(enumClass=ApplicationType.class)
	private ApplicationType application;

}
