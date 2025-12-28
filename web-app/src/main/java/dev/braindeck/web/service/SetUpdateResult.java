package dev.braindeck.web.service;

import java.util.Map;

public class SetUpdateResult {

    private final Map<String, String> errors;

    private SetUpdateResult(Map<String, String> errors) {
        this.errors = errors;
    }

    public static SetUpdateResult empty() {
        return new SetUpdateResult(null);
    }

    public static SetUpdateResult error(Map<String, String> errors) {
        return new SetUpdateResult(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
