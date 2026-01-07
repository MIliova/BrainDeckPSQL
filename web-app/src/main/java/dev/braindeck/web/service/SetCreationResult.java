package dev.braindeck.web.service;

import dev.braindeck.web.entity.SetCreatedDto;
import dev.braindeck.web.entity.SetFullDto;

import java.util.Map;

public class SetCreationResult {

    private final SetCreatedDto set;
    private final Map<String, String> errors;

    private SetCreationResult(SetCreatedDto set, Map<String, String> errors) {
        this.set = set;
        this.errors = errors;
    }

    public static SetCreationResult success(SetCreatedDto set) {

        return new SetCreationResult(set, null);
    }

    public static SetCreationResult error(Map<String, String> errors) {

        return new SetCreationResult(null, errors);
    }

    public boolean hasErrors() {

        return errors != null && !errors.isEmpty();
    }

    public SetCreatedDto getSet() {
        return set;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
