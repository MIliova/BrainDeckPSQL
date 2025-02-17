package dev.braindeck.web.controller.payload;

import dev.braindeck.web.entity.NewTerm;

import java.util.List;

public record RestNewSetPayload(

        String title,

        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId,

        List<NewTerm> terms


) {
}
