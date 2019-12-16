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
@Target( { ElementType.FIELD })
@Constraint(validatedBy = CustomNameValidatorImpl.class)
public @interface CustomNameValidator {
	String message() default "{error.user.name.invalid}";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
