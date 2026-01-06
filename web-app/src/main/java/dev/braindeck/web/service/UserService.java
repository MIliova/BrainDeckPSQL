package dev.braindeck.web.service;

import dev.braindeck.web.entity.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public UserDto getCurrentUser () {
        return new UserDto(1, "Marina");
    }

}
