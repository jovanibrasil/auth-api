package com.security.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.security.jwt.services.UserService;

public class UserEmailValidatorImpl implements ConstraintValidator<CustomEmailValidator, String> {

	private static final Logger log = LoggerFactory.getLogger(UserEmailValidatorImpl.class);
	
	private UserService userService;
	private StringValidations strVals;
	
	@Autowired
	public UserEmailValidatorImpl(UserService userService, StringValidations strVals) {
		this.userService = userService;
		this.strVals = strVals;
	}
	
	@Override
	public boolean isValid(@NotBlank String value, ConstraintValidatorContext context) {
		try {
			String message;
			if(strVals.isBlank(value)) {
				message = "Email must not be blank or null.";
			}else if(!strVals.hasValidSize(value, 0, 30)) {
				message = "Email length must be between 0 and 30.";
			}else if(!strVals.isEmail(value)) {
				message = "Email must be a well-formed email address.";
			}else {
				log.info("Validanting : {}", value);
				boolean isValid = !userService.findUserByEmail(value).isPresent();
				if(isValid) { 
					return true;
				}else {
					message = "This email already exists.";
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
