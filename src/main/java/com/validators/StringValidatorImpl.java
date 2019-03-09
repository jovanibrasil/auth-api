package com.validators;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringValidatorImpl implements ConstraintValidator<StringValidator, String> {

	private List<String> valueList;

	@Override
	public void initialize(StringValidator constraintAnnotation) {
		valueList = new ArrayList<String>();
		for (String val : constraintAnnotation.acceptedValues()) {
			valueList.add(val.toUpperCase());
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (!valueList.contains(value.toUpperCase())) {
			return false;
		}
		return true;
	}

}
