package dev.braindeck.web.entity;


public record Term (
         Integer id,
         Integer setId,
         String term,
         String description
) {
}
