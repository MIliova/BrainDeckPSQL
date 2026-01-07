package dev.braindeck.api.dto;

import java.util.List;

public record SetShortDto(
        Integer id,
        String title,
        String description,
        Integer userId,
        String userName,
        List<TermDto> terms
) {
}
