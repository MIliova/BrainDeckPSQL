package dev.braindeck.web.controller.payload;

public record UpdateSetPayload(
        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        Integer userId

) {
}
