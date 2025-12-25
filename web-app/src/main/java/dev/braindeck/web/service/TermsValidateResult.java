package dev.braindeck.web.service;

import dev.braindeck.web.controller.payload.NewTermPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermsValidateResult {

    private final Map<String, Object> modelAttributes;
    private final Map<String, String> errors;
    private final Map<Integer, Map<String, String>> termErrors;
    private final List<NewTermPayload> terms;

    public static TermsValidateResult generalError(String message) {
        return new TermsValidateResult(
                Map.of("general", message),
                Map.of(),
                List.of()
        );
    }

    public static TermsValidateResult termErrors(
            List<NewTermPayload> terms,
            Map<Integer, Map<String, String>> termErrors
    ) {
        return new TermsValidateResult(
                Map.of(),
                termErrors,
                terms
        );
    }

    public static TermsValidateResult success(List<NewTermPayload> terms) {
        return new TermsValidateResult(
                Map.of(),
                Map.of(),
                terms
        );
    }

    private TermsValidateResult(
            Map<String, String> errors,
            Map<Integer, Map<String, String>> termErrors,
            List<NewTermPayload> terms
    ) {
        this.errors = errors;
        this.termErrors = termErrors;
        this.terms = terms;

        Map<String, Object> attrs = new HashMap<>();
        if (!errors.isEmpty()) {
            attrs.put("errors", errors);
        }
        if (!termErrors.isEmpty()) {
            attrs.put("termErrors", termErrors);
        }
        if (!terms.isEmpty()) {
            attrs.put("terms", terms);
        }

        this.modelAttributes = Map.copyOf(attrs);
    }

    public boolean hasErrors() {
        return !errors.isEmpty() || !termErrors.isEmpty();
    }
    public Map<String, Object> getModelAttributes() {
        return modelAttributes;
    }
    public List<NewTermPayload> getTerms() {
        return terms;
    }

}