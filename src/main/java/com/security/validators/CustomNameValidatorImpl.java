package com.security.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.security.jwt.services.UserService;

public class CustomNameValidatorImpl implements ConstraintValidator<CustomNameValidator, String> {
	
	private static final Logger log = LoggerFactory.getLogger(CustomNameValidatorImpl.class);
	
	private UserService userService;
	private StringValidations strVals;
	
	@Autowired
	public CustomNameValidatorImpl(UserService userService, StringValidations strVals) {
		this.userService = userService;
		this.strVals = strVals;
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			String message;
			if(strVals.isBlank(value)) {
				message = "Username must not be blank or null.";
			}else if(!strVals.hasValidSize(value, 2, 12)) {
				message = "Username length must be between 2 and 12.";
			}else {
				log.info("Validanting : {}", value);
				boolean isValid = !userService.findByUserName(value).isPresent();
				if(isValid) { 
					return true;
				}else {
					message = "This user name already exists.";
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