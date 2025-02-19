package dev.braindeck.web.entity;


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

