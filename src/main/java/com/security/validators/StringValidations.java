package com.security.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringValidations {

	// Email Regex java
	private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

	// static Pattern object, since pattern is fixed
	private static Pattern pattern;

	// non-static Matcher object because it's created from the input String
	private Matcher matcher;

	public StringValidations() {
		// initialize the Pattern object
		pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
	}

	public boolean isNull(String value) {
		return value == null;
	}

	public boolean isBlank(String value) {
		if (value == null) return true;
		return value.trim().isEmpty();
	}

	public boolean hasValidSize(String value, int minSize, int maxSize) {
		if (value == null)
			return false;
		return !(value.length() < minSize || value.length() > maxSize);
	}

	public boolean isEmail(String value) {
		if (value == null)
			return false;
		matcher = pattern.matcher(value);
		return matcher.matches();
	}

}
