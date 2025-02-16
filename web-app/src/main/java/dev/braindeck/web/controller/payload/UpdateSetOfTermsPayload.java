package dev.braindeck.web.controller.payload;

public record UpdateSetOfTermsPayload(
        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId
) {
}
