package dev.braindeck.web.service;

import dev.braindeck.web.controller.FieldErrorDto;
import dev.braindeck.web.entity.SetDto;

import java.util.List;

public class SetCreationResult {

    private final SetDto set;
    private final List<FieldErrorDto> errors;

    private SetCreationResult(SetDto set, List<FieldErrorDto> errors) {
        this.set = set;
        this.errors = errors;
    }

    public static SetCreationResult success(SetDto set) {
        return new SetCreationResult(set, null);
    }

    public static SetCreationResult error(List<FieldErrorDto> errors) {
        return new SetCreationResult(null, errors);
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public SetDto getSet() {
        return set;
    }

    public List<FieldErrorDto> getErrors() {
        return errors;
    }
}
