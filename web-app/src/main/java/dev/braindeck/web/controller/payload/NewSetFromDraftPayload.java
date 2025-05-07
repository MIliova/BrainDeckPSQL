package dev.braindeck.web.controller.payload;

public record NewSetFromDraftPayload(

        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId

) {
}
