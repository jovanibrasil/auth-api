package com.security.web.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class EnumListValidatorImpl implements ConstraintValidator<EnumListValidator, ArrayList<String>> {

	private List<String> valueList;

	@Override
	public void initialize(EnumListValidator constraintAnnotation) {
		valueList = new ArrayList<String>();
	    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
	    for(Enum enumVal : enumClass.getEnumConstants()) {
	      valueList.add(enumVal.toString().toUpperCase());
	    }
	}

	@Override
	public boolean isValid(ArrayList<String> values, ConstraintValidatorContext context) {
		for (String value : values) {
			if (!valueList.contains(value.toUpperCase())) {
				return false;
			}
		}
		return true;
	}

}
