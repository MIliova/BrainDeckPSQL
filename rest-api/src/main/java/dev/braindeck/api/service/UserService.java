package dev.braindeck.api.service;

import dev.braindeck.api.entity.User;

public interface UserService {
    User findById(int id);
}
