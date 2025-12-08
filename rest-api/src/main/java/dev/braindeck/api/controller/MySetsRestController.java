package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.controller.payload.UpdateSetPayload;
import dev.braindeck.api.dto.*;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/sets")
public class MySetsRestController {

    private final UserService userService;
    private final SetService setService;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {
        System.out.println(payload);
        UserEntity user = userService.getCurrentUser();
        SetDto set = setService.create(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/sets/{setId}").build(Map.of("setId", set.id())))
                .body(set);
    }

    @PatchMapping("/{setId:\\d+}")
    public ResponseEntity<Void> update(@PathVariable("setId") int setId,
                                       @Valid @RequestBody UpdateSetPayload payload) {
        System.out.println(payload);
        UserEntity user = userService.getCurrentUser();
        setService.update(setId, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{setId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("setId") int setId) {
        System.out.println("Deleting set " + setId);
        setService.delete(setId);
        return ResponseEntity.noContent().build();
    }

}
