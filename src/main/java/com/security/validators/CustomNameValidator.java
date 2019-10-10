package com.security.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME) // the annotation will be available at runtime by means of reflection
@Constraint(validatedBy = UserNameValidatorImpl.class)
@Target( { ElementType.FIELD })
@NotBlank(message = "Username must not be null or blank.")
@Size(min=2, max=12, message="Username length must be between 2 and 12.")
public @interface CustomNameValidator {
	String message() default "Username is not valid.";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
