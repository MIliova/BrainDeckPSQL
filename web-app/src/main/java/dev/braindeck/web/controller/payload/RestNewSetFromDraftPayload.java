package dev.braindeck.web.controller.payload;

import dev.braindeck.web.entity.TermDto;

import java.util.List;

public record RestNewSetFromDraftPayload(
        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<NewTermPayload> terms
) {
}
