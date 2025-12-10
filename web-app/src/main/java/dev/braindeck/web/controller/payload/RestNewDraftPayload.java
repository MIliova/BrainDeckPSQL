package dev.braindeck.web.controller.payload;

import java.util.List;

public record RestNewDraftPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId

) {
}
