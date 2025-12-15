package dev.braindeck.web.entity;


public record NewDTermDto(
        Integer draftId,
        Integer id,
        String term,
        String description
) {
}
