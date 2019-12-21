package com.security.validators;

import com.security.jwt.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class CustomNameValidatorImpl implements ConstraintValidator<CustomNameValidator, String> {

	private UserService userService;
	private StringValidations strVals;

	public CustomNameValidatorImpl(UserService userService, StringValidations strVals) {
		this.userService = userService;
		this.strVals = strVals;
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			String message;
			if(strVals.isBlank(value)) {
				message = "{error.user.name.notblank}";
			}else if(!strVals.hasValidSize(value, 2, 12)) {
				message = "{error.user.name.size}";
			}else {
				log.info("Validating : {}", value);
				boolean isValid = !userService.findByUserName(value).isPresent();
				if(isValid) { 
					return true;
				}else {
					message = "{error.user.name.unique}";
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
