package com.security.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@NotBlank(message = "Email must not be null or blank.")
@Email(message="Email must be a well-formed email address.")
@Size(max = 30, message = "Email length must be lower than 30 characters.")
@Constraint(validatedBy = UserEmailValidatorImpl.class)
public @interface CustomEmailValidator {
	String message() default "Email is not valid.";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
