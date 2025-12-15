package dev.braindeck.web.controller.payload;

import jakarta.validation.constraints.Size;

public record DraftPayload(

        @Size(max = 256, message = "{error.set.title.size}")
        String title,

        @Size(max = 500, message = "{error.set.description.size}")
        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId

) {
}
