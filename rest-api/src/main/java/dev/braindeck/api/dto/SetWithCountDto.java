package dev.braindeck.api.dto;

public record SetWithCountDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        UserDto user,
        Long termCount
) {
}

