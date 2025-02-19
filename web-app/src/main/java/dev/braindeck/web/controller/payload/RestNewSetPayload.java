package dev.braindeck.web.controller.payload;

import java.util.List;

public record RestNewSetPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<NewTermPayload> terms


) {
}
