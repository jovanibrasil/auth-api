package com.security.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.security.jwt.services.UserService;

public class UserEmailValidatorImpl implements ConstraintValidator<UserEmailValidator, String> {

	private UserService userService;
	
	@Autowired
	public UserEmailValidatorImpl(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			boolean isValid = !userService.findUserByEmail(value).isPresent();
			
			if(isValid) return true;
			
			context.disableDefaultConstraintViolation();
            context
            	.buildConstraintViolationWithTemplate("This email already exists.")
            	.addConstraintViolation();
            
            return false;
		} catch (Exception e) {
			return false;
		}
	}
	
}
