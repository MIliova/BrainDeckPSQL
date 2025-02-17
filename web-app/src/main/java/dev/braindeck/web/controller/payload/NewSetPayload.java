package dev.braindeck.web.controller.payload;

public record NewSetPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        Integer userId
) {
}
