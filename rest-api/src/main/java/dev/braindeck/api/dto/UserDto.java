package dev.braindeck.api.dto;

public record UserDto(
        Integer id,
        String name,
        String email,
        String password
) {
}
