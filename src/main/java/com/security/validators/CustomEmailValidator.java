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
@Constraint(validatedBy = UserEmailValidatorImpl.class)
public @interface CustomEmailValidator {
	String message() default "Email is not valid.";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
