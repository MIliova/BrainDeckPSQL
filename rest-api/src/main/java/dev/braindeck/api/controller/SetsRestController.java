package dev.braindeck.api.controller;

import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithCountDto;
import dev.braindeck.api.dto.UserDto;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SetsRestController {

    private final UserService userService;
    private final SetService setService;

    @GetMapping("/current-user")
    public UserDto currentUser() {
        return userService.findCurrentUser();
    }

    @GetMapping("/users/{userId:\\d+}/sets")
    public List<SetWithCountDto> findSets(@PathVariable("userId") int userId) {
        return this.setService.findAllByUserId(userId);
    }

    @GetMapping("/sets/{setId:\\d+}")
    public SetDto findSet(@PathVariable("setId") int setId) {
        return this.setService.findSetById(setId);
    }





}
