package dev.braindeck.web.service;

import dev.braindeck.web.controller.payload.TermItemVM;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermsValidateResult<T> {

    @Getter
    private final List<TermItemVM<T>> termsVM;

    private final boolean hasTermsErrors;

    @Getter
    private final Map<String, String> error;

    @Getter
    private final List<T> terms;


//    private final Map<String, Object> modelAttributes;

    public static <T> TermsValidateResult<T> generalError(String message) {
        return new TermsValidateResult<>(
                Map.of("terms", message),
                List.of(),
                false,
                List.of()
                );
    }

    public static <T> TermsValidateResult<T> from(TermParser.ParseResult<T> parseResult) {
        return new TermsValidateResult<>(
                Map.of(),
                parseResult.termsVM(),
                parseResult.hasError(),
                parseResult.terms()
        );
    }

    private TermsValidateResult(Map<String, String> error, List<TermItemVM<T>> termsVM, boolean hasTermsErrors, List<T> terms) {
        this.error = error;
        this.termsVM = termsVM;
        this.hasTermsErrors = hasTermsErrors;
        this.terms = terms;


//        Map<String, Object> attrs = new HashMap<>();
//        if (!errors.isEmpty()) {
//            attrs.put("errors", errors);
//        }
//        if (hasTermsErrors) {
//            attrs.put("hasTermsErrors", hasTermsErrors);
//        }
//        if (!terms.isEmpty()) {
//            attrs.put("terms", terms);
//        }


//        this.modelAttributes = Map.copyOf(attrs);
    }

    public boolean hasErrors() {
        return !error.isEmpty() || hasTermsErrors;
    }

}
