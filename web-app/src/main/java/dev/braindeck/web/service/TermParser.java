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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermParser {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public List<NewTermPayload> parse(String json) {
        final List<NewTermPayload> terms;

        try {
            terms = objectMapper.readValue(
                    json,
                    new TypeReference<List<NewTermPayload>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid terms JSON", e);
        }

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("terms must not be empty");
        }

        Set<ConstraintViolation<NewTermPayload>> violations = terms.stream()
                .flatMap(term -> validator.validate(term).stream())
                .collect(Collectors.toSet());

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return terms;
    }
    public List<UpdateTermPayload> parseForUpdate(String json) {
        final List<UpdateTermPayload> terms;

        try {
            terms = objectMapper.readValue(
                    json,
                    new TypeReference<List<UpdateTermPayload>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid terms JSON", e);
        }

        if (terms.isEmpty()) {
            throw new IllegalArgumentException("terms must not be empty");
        }

        Set<ConstraintViolation<UpdateTermPayload>> violations = terms.stream()
                .flatMap(term -> validator.validate(term).stream())
                .collect(Collectors.toSet());

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return terms;
    }
}


