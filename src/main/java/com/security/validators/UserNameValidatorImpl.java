package com.security.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.security.jwt.services.UserService;

public class UserNameValidatorImpl implements ConstraintValidator<UserNameValidator, String> {
	
	private UserService userService;
	
	@Autowired
	public UserNameValidatorImpl(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			boolean isValid = !userService.findByUserName(value).isPresent();
			
			if(isValid) return true;
			
			context.disableDefaultConstraintViolation();
            context
            	.buildConstraintViolationWithTemplate("This user name already exists.")
            	.addConstraintViolation();
            
            return false;
		} catch (Exception e) {
			System.out.println("username não é válido");
			return false;
		}
	}

}
