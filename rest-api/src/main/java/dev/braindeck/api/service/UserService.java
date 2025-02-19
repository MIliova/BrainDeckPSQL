package dev.braindeck.api.service;

import dev.braindeck.api.entity.User;
import dev.braindeck.api.entity.UserDto;

public interface UserService {
    User findById(int id);
    UserDto findCurrentUser ();

}
