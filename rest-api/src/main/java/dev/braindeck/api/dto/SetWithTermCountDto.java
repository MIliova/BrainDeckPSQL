package dev.braindeck.api.dto;

import java.time.Instant;

public record SetWithTermCountDto(
        Integer id,
        String title,
        String description,
        Instant updatedAt,
        Long termCount
) {
}

