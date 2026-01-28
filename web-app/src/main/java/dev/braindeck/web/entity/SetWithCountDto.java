package dev.braindeck.web.entity;

public record SetWithCountDto(
        Integer id,
        String title,
        String description,
        String updatedAt,
        Long termCount,
        Integer userId,
        String userName
) {
}



