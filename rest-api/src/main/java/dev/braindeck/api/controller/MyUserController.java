package dev.braindeck.api.controller;

import dev.braindeck.api.dto.UserDto;
import dev.braindeck.api.service.Mapper;
import dev.braindeck.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyUserController {

    private final UserService userService;

    @GetMapping
    public UserDto get() {
        return userService.getCurrent();
    }

}
