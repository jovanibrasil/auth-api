package com.security.web.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Enum<?>> {

	private List<String> values; // String array of possible enum values

    @Override
    public void initialize(EnumValidator constraintAnnotation){
    	values = new ArrayList<String>();
	    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
	    for(Enum<?> enumVal : enumClass.getEnumConstants()) {
	    	values.add(enumVal.toString().toUpperCase());
	    }
    }
	
	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
		if(value == null) {
			context.disableDefaultConstraintViolation();
	        context
	        	.buildConstraintViolationWithTemplate("{error.application.notnull}")
	        	.addConstraintViolation();
			return false;
		}
		return values.contains(value.toString());
	}	
		
}
