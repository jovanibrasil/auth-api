package com.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target( { ElementType.FIELD })
@NotNull
@Email(message="Email must be a well-formed email address.")
@Constraint(validatedBy = UserEmailValidatorImpl.class)
public @interface UserEmailValidator {
	String message() default "Email is not valid.";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
