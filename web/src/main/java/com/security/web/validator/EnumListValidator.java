package com.security.web.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
@Constraint(validatedBy = EnumListValidatorImpl.class)
@NotNull(message = "{error.applications.notnull}")
@NotEmpty(message = "{error.applications.notempty}")
public @interface EnumListValidator {
	String message() default "{error.applications.invalid}";
    Class<?>[] groups() default { };
    Class<? extends Enum<?>> enumClass();
    Class<? extends Payload>[] payload() default { };
}
