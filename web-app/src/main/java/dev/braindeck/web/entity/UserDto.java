package dev.braindeck.web.entity;

public record UserDto (
        Integer id,
        String name,
        String email,
        String password
) {
}
