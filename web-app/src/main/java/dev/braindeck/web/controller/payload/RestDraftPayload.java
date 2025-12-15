package dev.braindeck.web.controller.payload;

import java.util.List;

public record RestDraftPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<DTermPayload> terms

) {
}
