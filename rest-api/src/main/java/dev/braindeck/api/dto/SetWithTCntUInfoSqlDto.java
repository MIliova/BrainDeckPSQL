package dev.braindeck.api.dto;

import java.time.Instant;

public record SetWithTCntUInfoSqlDto(
        Integer id,
        String title,
        String description,
        Instant updatedAt,
        Long termCount,
        Integer userId,
        String userName
) {
}

