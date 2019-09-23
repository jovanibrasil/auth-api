package com.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UserNameValidatorImpl.class)
@Target( { ElementType.FIELD })
@NotNull
@Size(min=2, max=10, message="Username length must be between 2 and 10.")
public @interface UserNameValidator {
	String message() default "Username is not valid.";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
