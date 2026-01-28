package dev.braindeck.web.controller.payload;

import java.util.List;

public record RestUpdateSetPayload(

        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<UpdateTermPayload> terms

) {
}
