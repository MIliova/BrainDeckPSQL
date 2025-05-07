package dev.braindeck.web.entity;


public record TermDto(
         Integer id,
         Integer setId,
         String term,
         String description
) {
}
