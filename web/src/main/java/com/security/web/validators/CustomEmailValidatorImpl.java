package com.security.web.validators;

import com.security.web.services.UserService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotBlank;

@Slf4j
public class CustomEmailValidatorImpl implements ConstraintValidator<CustomEmailValidator, String> {

	private UserService userService;
	private StringValidations strVals;

	public CustomEmailValidatorImpl(UserService userService, StringValidations strVals) {
		this.userService = userService;
		this.strVals = strVals;
	}
	
	@Override
	public boolean isValid(@NotBlank String value, ConstraintValidatorContext context) {
		try {
			String message;
			if(strVals.isBlank(value)) {
				message = "{error.email.notnull}";
			}else if(!strVals.hasValidSize(value, 0, 30)) {
				message = "{error.email.size}";
			}else if(!strVals.isEmail(value)) {
				message = "{error.email.invalid}";
			}else {
				log.info("Validating : {}", value);
				if(!userService.existUserWithEmail(value)) {
					return true;
				}else {
					message = "{error.email.alreadyexists}";
				}
			}
			context.disableDefaultConstraintViolation();
            context
            	.buildConstraintViolationWithTemplate(message)
            	.addConstraintViolation();
            return false;
		} catch (Exception e) {
			return false;
		}
	}
	
}