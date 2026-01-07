package dev.braindeck.api.dto;

public record NewDraftDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId
) {
}
