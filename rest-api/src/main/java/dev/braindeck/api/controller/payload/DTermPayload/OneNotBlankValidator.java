package dev.braindeck.api.controller.payload.DTermPayload;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneNotBlankValidator implements ConstraintValidator<OneNotBlank, DTermPayload> {

    @Override
    public boolean isValid(DTermPayload o, ConstraintValidatorContext constraintValidatorContext) {
        String term = o.term() != null ? o.term().trim() : "";
        String description = o.description() != null ? o.description().trim() : "";
        return !term.isBlank() || !description.isBlank();    }
}
