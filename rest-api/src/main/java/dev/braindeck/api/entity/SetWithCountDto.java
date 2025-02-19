package dev.braindeck.api.entity;


import java.util.List;

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

