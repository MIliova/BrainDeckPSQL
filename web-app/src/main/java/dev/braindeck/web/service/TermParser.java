package dev.braindeck.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.controller.payload.TermItemVM;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TermParser {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public record ParseResult<T>(
            List<TermItemVM<T>> termsVM,
            boolean hasError,
            List<T> terms
    ) {}

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

        List<TermItemVM<T>> items = new ArrayList<>();
        boolean hasError = false;

        for (T term : terms) {
            TermItemVM<T> item = new TermItemVM<>(term);
            Set<ConstraintViolation<T>> violations = validator.validate(term);

            if (!violations.isEmpty()) {

                item.setHasErrors(true);
                hasError = true;

                Map<String, String> errors = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage,
                                (existing, replacement) -> existing + "; " + replacement
                        ));
                item.setErrors(errors);

            }
            items.add(item);

        }

        return new ParseResult<>(items, hasError, terms);
    }


}

