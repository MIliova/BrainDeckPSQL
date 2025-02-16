package dev.braindeck.web.controller.payload;

import dev.braindeck.web.entity.Term;

import java.util.List;

public record RestUpdateSetPayload(
        Integer id,

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<Term> terms
) {
}
