package dev.braindeck.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
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


    public ParseResult parse(String json) {
        List<NewTermPayload> terms;

        try {
            terms = objectMapper.readValue(json, new TypeReference<List<NewTermPayload>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("Terms must not be empty");
        }

        Map<Integer, Map<String, String>> termErrors = new HashMap<>();

        for (int i = 0; i < terms.size(); i++) {
            NewTermPayload term = terms.get(i);
            Set<ConstraintViolation<NewTermPayload>> violations = validator.validate(term);

            if (!violations.isEmpty()) {
                Map<String, String> fieldErrors = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage
                        ));
                termErrors.put(i, fieldErrors);
            }
        }

        return new ParseResult(terms, termErrors);
    }

    public static class ParseResult {
        private final List<NewTermPayload> terms;
        private final Map<Integer, Map<String, String>> termErrors;

        public ParseResult(List<NewTermPayload> terms, Map<Integer, Map<String, String>> termErrors) {
            this.terms = terms;
            this.termErrors = termErrors;
        }

        public List<NewTermPayload> getTerms() {
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

//public class TermParser {
//
//    private final ObjectMapper objectMapper;
//    private final Validator validator;
//
//    public List<NewTermPayload> parse(String json) {
//        final List<NewTermPayload> terms;
//
//        try {
//            terms = objectMapper.readValue(
//                    json,
//                    new TypeReference<List<NewTermPayload>>() {}
//            );
//        } catch (JsonProcessingException e) {
//            throw new IllegalArgumentException("Invalid terms JSON", e);
//        }
//
//        if (terms.isEmpty()) {
//            throw new IllegalArgumentException("terms must not be empty");
//        }
//
//        Set<ConstraintViolation<NewTermPayload>> violations = terms.stream()
//                .flatMap(term -> validator.validate(term).stream())
//                .collect(Collectors.toSet());
//
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(violations);
//        }
//
//        return terms;
//    }
//
//    public List<UpdateTermPayload> parseForUpdate(String json) {
//        final List<UpdateTermPayload> terms;
//
//        try {
//            terms = objectMapper.readValue(
//                    json,
//                    new TypeReference<List<UpdateTermPayload>>() {}
//            );
//        } catch (JsonProcessingException e) {
//            throw new IllegalArgumentException("Invalid terms JSON", e);
//        }
//
//        if (terms.isEmpty()) {
//            throw new IllegalArgumentException("terms must not be empty");
//        }
//
//        Set<ConstraintViolation<UpdateTermPayload>> violations = terms.stream()
//                .flatMap(term -> validator.validate(term).stream())
//                .collect(Collectors.toSet());
//
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(violations);
//        }
//
//        return terms;
//    }
//}


