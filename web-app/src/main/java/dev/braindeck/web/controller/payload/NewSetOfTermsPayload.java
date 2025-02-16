package dev.braindeck.web.controller.payload;

public record NewSetOfTermsPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId
) {
}
