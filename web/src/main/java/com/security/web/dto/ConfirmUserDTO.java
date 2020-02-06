package com.security.web.dto;

import com.security.web.domain.ApplicationType;
import com.security.web.validators.CustomEmailValidator;
import com.security.web.validators.EnumValidator;
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
