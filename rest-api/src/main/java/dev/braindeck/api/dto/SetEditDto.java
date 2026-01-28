package dev.braindeck.api.dto;

import java.util.List;

public record SetEditDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        List<TermDto> terms
) {
}
