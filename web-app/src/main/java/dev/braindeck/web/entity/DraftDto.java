package dev.braindeck.web.entity;

import java.util.List;

public record DraftDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        List<TermDto> terms
) {
}
