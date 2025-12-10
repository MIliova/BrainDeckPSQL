package dev.braindeck.web.entity;

public record NewDraftDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId
) {
}
