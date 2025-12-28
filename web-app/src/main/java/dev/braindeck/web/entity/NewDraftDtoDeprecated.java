package dev.braindeck.web.entity;

public record NewDraftDtoDeprecated(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId
) {
}
