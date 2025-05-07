package dev.braindeck.api.dto;

import java.util.List;

public record DraftDto(
        Integer id,
        Integer userId,
        List<TermDto> terms
) {
}
