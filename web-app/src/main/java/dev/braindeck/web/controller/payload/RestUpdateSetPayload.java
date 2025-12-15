package dev.braindeck.web.controller.payload;

import dev.braindeck.web.entity.TermDto;

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
