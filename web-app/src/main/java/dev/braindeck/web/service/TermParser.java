package dev.braindeck.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TermParser {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public TermParser(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public <T> ParseResult<T> parse(String json, Class<T> termClass) {
        List<T> terms;
        try {
            terms = objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, termClass)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("Terms must not be empty");
        }

        Map<Integer, Map<String, String>> termErrors = new HashMap<>();

        for (int i = 0; i < terms.size(); i++) {
            T term = terms.get(i);
            Set<ConstraintViolation<T>> violations = validator.validate(term);

            if (!violations.isEmpty()) {
                System.out.println(violations);

                Map<String, String> fieldErrors = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage,
                                (existing, replacement) -> existing + "; " + replacement
                        ));
                termErrors.put(i, fieldErrors);
            }
        }

        return new ParseResult<>(terms, termErrors);
    }

    public static class ParseResult<T> {
        private final List<T> terms;
        private final Map<Integer, Map<String, String>> termErrors;

        public ParseResult(List<T> terms, Map<Integer, Map<String, String>> termErrors) {
            this.terms = terms;
            this.termErrors = termErrors;
        }

        public List<T> getTerms() {
            return terms;
        }

        public Map<Integer, Map<String, String>> getTermErrors() {
            return termErrors;
        }

        public boolean hasErrors() {
            return !termErrors.isEmpty();
        }
    }
}
