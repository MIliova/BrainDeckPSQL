package dev.braindeck.api.service;

import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.dto.UserDto;

public interface UserService {
    UserEntity findById(int id);
    UserDto findCurrentUser ();
    //UserWithDraftDto findByIdWithDraft (int id);
}
