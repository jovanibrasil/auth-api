package com.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

@Constraint(validatedBy = StringValidatorImpl.class)
@NotNull(message = "Value cannot be null.")
public @interface StringValidator {
	String[] acceptedValues();
    String message() default "Value is not valid.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
