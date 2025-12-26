package dev.braindeck.web.service;

import dev.braindeck.web.controller.FieldErrorDto;

import java.util.List;

public class SetUpdateResult {

    private final List<FieldErrorDto> errors;

    private SetUpdateResult(List<FieldErrorDto> errors) {
        this.errors = errors == null ? List.of() : errors;
    }

    public static SetUpdateResult empty() {
        return new SetUpdateResult(List.of());
    }

    public static SetUpdateResult error(List<FieldErrorDto> errors) {
        return new SetUpdateResult(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<FieldErrorDto> getErrors() {
        return errors;
    }
}
