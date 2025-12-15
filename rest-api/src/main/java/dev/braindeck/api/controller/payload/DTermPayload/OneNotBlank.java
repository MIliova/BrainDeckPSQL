package dev.braindeck.api.controller.payload.DTermPayload;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = OneNotBlankValidator.class)
@Documented
public @interface OneNotBlank {
    String message() default "At least one field must be not blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
