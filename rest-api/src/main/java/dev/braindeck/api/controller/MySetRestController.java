package dev.braindeck.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.controller.payload.UpdateSetPayload;
import dev.braindeck.api.dto.*;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/set")
public class MySetRestController {

    private final UserService userService;
    private final SetService setService;

    @GetMapping("/{setId:\\d+}")
    public ResponseEntity<SetEditDto> findMySet(
            @PathVariable("setId") @Positive (message = "errors.set.id") int setId) {

        UserEntity user = userService.getCurrentUser();
        SetEditDto setEditDto = setService.findSetEditDtoById(user.getId(), setId);

        return ResponseEntity.ok(setEditDto);
    }

    @PostMapping
    public ResponseEntity<SetCreatedDto> create(
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {

        System.out.println("create");

        UserEntity user = userService.getCurrentUser();

        SetCreatedDto set = setService.create(
                user.getId(),
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId(),
                payload.terms());
        return ResponseEntity.created(
                uriBuilder
                        .replacePath("/api/users/me/set/{setId}")
                        .build(Map.of("setId", set.id())))
                .body(set);
    }

    @PatchMapping("/{setId:\\d+}/autosave")
    public ResponseEntity<Void> autoUpdate(
            @PathVariable("setId") @Positive (message = "errors.set.id") int setId,
            @RequestBody JsonNode body) {

        UserEntity user = userService.getCurrentUser();
        setService.autoUpdate(
                user.getId(),
                setId,
                body);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{setId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable("setId") @Positive (message = "errors.set.id") int setId,
            @Valid @RequestBody UpdateSetPayload payload) {
        UserEntity user = userService.getCurrentUser();
        setService.update(
                setId,
                payload.title(), payload.description(),
                payload.termLanguageId(), payload.descriptionLanguageId(),
                payload.terms(), user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{setId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable("setId") @Positive (message = "errors.set.id") int setId) {
        UserEntity user = userService.getCurrentUser();
        setService.delete(setId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
