package dev.braindeck.api.controller.payload.DTermPayload;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneNotBlankValidator implements ConstraintValidator<OneNotBlank, DTermPayload> {

    @Override
    public boolean isValid(DTermPayload o, ConstraintValidatorContext context) {
        if (o == null) {
            return false;
        }
        String term = o.term() != null ? o.term().trim() : "";
        String description = o.description() != null ? o.description().trim() : "";
        boolean valid = !term.isBlank() || !description.isBlank();

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            "term or description must not be blank"
                    )
                    .addPropertyNode("term")
                    .addConstraintViolation();
        }

        return valid;
    }
}
