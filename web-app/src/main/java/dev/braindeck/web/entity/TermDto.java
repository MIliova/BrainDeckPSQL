package dev.braindeck.web.entity;


public record TermDto(
        Integer setId,
        Integer id,
        String term,
        String description
) {
}
