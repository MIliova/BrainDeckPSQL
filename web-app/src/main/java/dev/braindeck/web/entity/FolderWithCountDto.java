package dev.braindeck.web.entity;

public record FolderWithCountDto(
        Integer id,
        String title,
        Long termCount,
        Integer userId,
        String userName
) {
}



