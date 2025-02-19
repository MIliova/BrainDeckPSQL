package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.entity.SetDto;
import dev.braindeck.api.entity.User;
import dev.braindeck.api.entity.UserDto;
import dev.braindeck.api.service.Mapper;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/user/{userId:\\d+}/sets")
    public List<SetDto> findSets(@PathVariable("userId") int userId) {
        System.out.println("2="+userId);
        return this.setService.findAllByUserId(userId);
    }

    @PostMapping("/create-set")
    public ResponseEntity<?> createSet(@Valid @RequestBody NewSetPayload payload,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriBuilder) throws BindException {
        System.out.println(payload);
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            User user = userService.findById(1);
            SetDto set = this.setService.createSet(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
            return ResponseEntity.created(uriBuilder
                    .replacePath("/api/sets/{setId}").build(Map.of("setId", set.id())))
                    .body(set);
        }
    }
}
