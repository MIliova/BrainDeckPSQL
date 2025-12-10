package dev.braindeck.web.controller.payload;

public record NewDraftPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId

) {
}
