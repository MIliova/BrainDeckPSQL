package dev.braindeck.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermsValidateResult<T> {

    private final Map<String, Object> modelAttributes;
    private final Map<String, String> errors;
    private final Map<Integer, Map<String, String>> termErrors;
    private final List<T> terms;

    public static <T> TermsValidateResult<T> generalError(String message) {
        return new TermsValidateResult<>(
                Map.of("terms", message),
                Map.of(),
                List.of()
        );
    }

    public static <T> TermsValidateResult<T> termErrors(
            List<T> terms,
            Map<Integer, Map<String, String>> termErrors
    ) {
        return new TermsValidateResult<>(
                Map.of(),
                termErrors,
                terms
        );
    }

    public static <T> TermsValidateResult<T> success(List<T> terms) {
        return new TermsValidateResult<>(
                Map.of(),
                Map.of(),
                terms
        );
    }

    private TermsValidateResult(
            Map<String, String> errors,
            Map<Integer, Map<String, String>> termErrors,
            List<T> terms
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

    public List<T> getTerms() {
        return terms;
    }

}