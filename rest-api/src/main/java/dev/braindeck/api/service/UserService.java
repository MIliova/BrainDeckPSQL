package dev.braindeck.api.service;

import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.dto.UserDto;

public interface UserService {
    UserEntity findById(int id);
    UserEntity getCurrentUser();
    UserDto getCurrent();
}
