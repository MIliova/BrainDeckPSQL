package dev.braindeck.api.dto;

import java.util.List;

public record SetDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        UserDto user,
        List<TermDto> terms
) {
}
