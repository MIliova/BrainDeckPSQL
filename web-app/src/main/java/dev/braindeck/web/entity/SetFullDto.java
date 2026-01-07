package dev.braindeck.web.entity;

import java.util.List;

public record SetFullDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        UserDto user,
        List<TermDto> terms
) {

}
