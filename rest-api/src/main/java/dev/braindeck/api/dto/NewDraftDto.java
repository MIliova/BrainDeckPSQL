package dev.braindeck.api.dto;

import java.util.List;

public record NewDraftDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId
) {
}
