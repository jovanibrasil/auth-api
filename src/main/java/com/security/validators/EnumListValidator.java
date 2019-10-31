package com.security.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@Constraint(validatedBy = EnumListValidatorImpl.class)
@NotNull(message = "Applications cannot be null.")
@NotEmpty(message = "Applications must not be empty.")
public @interface EnumListValidator {
	String message() default "Applications is not valid.";
    Class<?>[] groups() default { };
    Class<? extends Enum<?>> enumClass();
    Class<? extends Payload>[] payload() default { };
}
