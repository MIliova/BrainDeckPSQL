package dev.braindeck.web.controller.payload;

import java.util.List;

public record RestSetPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<NewTermPayload> terms

) {
}
