package dev.braindeck.web.controller;

public record FieldErrorDto(
        String field,
        String message
) {

    @Override
    public String toString() {
        return "FieldErrorDTO{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
