package com.security.web.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // the annotation will be available at runtime by means of reflection
@Target( { ElementType.FIELD })
@Constraint(validatedBy = CustomNameValidatorImpl.class)
public @interface CustomNameValidator {
	String message() default "{error.user.name.invalid}";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
