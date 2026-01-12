package com.talentstream.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidQuestionValidator.class)
@Documented
public @interface ValidQuestion {
    String message() default "Invalid question configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
