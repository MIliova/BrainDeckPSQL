package dev.braindeck.api.controller.payload.TermsImportPayload;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.Set;

public class ImportValidator implements ConstraintValidator<ImportValidation, TermsImportPayload> {

    private static final Set<String> VALID_COL_OPTIONS = Set.of("tab", "comma", "custom");
    private static final Set<String> VALID_ROW_OPTIONS = Set.of("newline", "semicolon", "custom");

    @Override
    public boolean isValid(TermsImportPayload payload, ConstraintValidatorContext constraintValidatorContext) {

        System.out.println("payload from validator=" + payload);

        boolean valid = true;

        if (payload.colSeparator() == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Разделитель между Термином и Определением не может быть пустым")
                    .addPropertyNode("colSeparator")
                    .addConstraintViolation();
            valid = false;
        }
        if (payload.rowSeparator() == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Разделитель между различными Терминами не может быть пустым")
                    .addPropertyNode("rowSeparator")
                    .addConstraintViolation();

        }
        if (payload.colSeparator() != null && !VALID_COL_OPTIONS.contains(payload.colSeparator())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Разделитель между Термином и Определением указан некорректно: "
                    + payload.colSeparator())
                    .addPropertyNode("colSeparator")
                    .addConstraintViolation();
            valid = false;
        }

        if (payload.rowSeparator() != null && !VALID_ROW_OPTIONS.contains(payload.rowSeparator())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Разделитель между различными Терминами указан некорректно: "
                    + payload.rowSeparator())
                    .addPropertyNode("rowSeparator")
                    .addConstraintViolation();
            valid = false;
        }

        if (Objects.equals(payload.colSeparator(), "custom") && payload.colCustom() == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Укажите свой разделитель между Термином и Определением")
                    .addPropertyNode("colCustom")
                    .addConstraintViolation();
            valid = false;
        }
        if (Objects.equals(payload.rowSeparator(), "custom") && payload.rowCustom() == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Укажите свой разделитель между различными Терминами")
                    .addPropertyNode("rowCustom")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
