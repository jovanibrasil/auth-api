package com.security.validators;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Enum<?>> {

	private List<String> values; // String array of possible enum values

    @Override
    public void initialize(EnumValidator constraintAnnotation){
    	values = new ArrayList<String>();
	    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
	    for(Enum enumVal : enumClass.getEnumConstants()) {
	    	values.add(enumVal.toString().toUpperCase());
	    }
    }
	
	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
		if(value == null) {
			context.disableDefaultConstraintViolation();
	        context
	        	.buildConstraintViolationWithTemplate("Application cannot be null.")
	        	.addConstraintViolation();
			return false;
		}
		return values.contains(value.toString());
	}	
		
}