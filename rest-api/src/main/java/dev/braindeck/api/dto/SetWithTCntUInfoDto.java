package dev.braindeck.api.dto;

public record SetWithTCntUInfoDto(
        Integer id,
        String title,
        String description,
        String updatedAt,
        Long termCount,
        Integer userId,
        String userName
) {
}

