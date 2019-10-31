package com.security.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {
	String message() default "Application is not valid.";
    Class<?>[] groups() default { };
    Class<? extends Enum<?>> enumClass();
    Class<? extends Payload>[] payload() default { };
}
