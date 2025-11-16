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
    public ResponseEntity<?> createSet(
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {
        System.out.println(payload);
        UserEntity user = userService.findById(1);
        SetDto set = this.setService.createSet(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/sets/{setId}").build(Map.of("setId", set.id())))
                .body(set);
    }

    @PatchMapping("/{setId:\\d+}")
    public ResponseEntity<Void> updateSet(@PathVariable("setId") int setId,
                                          @Valid @RequestBody UpdateSetPayload payload) {
        System.out.println(payload);
        UserEntity user = userService.findById(1);
        this.setService.updateSet(setId, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{setId:\\d+}")
    public ResponseEntity<Void> deleteSet(@PathVariable("setId") int setId) {
        this.setService.deleteSet(setId);
        return ResponseEntity.noContent().build();
    }



}
