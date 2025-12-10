package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewDraftPayload;
import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.controller.payload.UpdateSetPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.dto.NewDraftDto;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/draft")
@CrossOrigin(origins = "http://localhost:8080")
public class MyDraftRestController {

    private final UserService userService;
    private final DraftService draftService;
    private final SetService setService;

    @PostMapping(value = "")
    public ResponseEntity<NewDraftDto> create(@Valid @RequestBody NewDraftPayload payload) {
        UserEntity user = userService.getCurrentUser();
        NewDraftDto draft = draftService.create(payload, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }

    @PatchMapping("/{draftId:\\d+}")
    public ResponseEntity<Void> update(@PathVariable int draftId,
                                       @Valid @RequestBody UpdateSetPayload payload) {
        UserEntity user = userService.getCurrentUser();
        draftService.update(draftId, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{draftId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable int draftId) {
        UserEntity user = userService.getCurrentUser();
        draftService.delete(draftId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<DraftSetDto> get() {
        UserEntity user = userService.getCurrentUser();
        DraftSetDto draft = draftService.findFirstByUserId(user.getId());
        if (draft == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(draft);
    }

//    @GetMapping("/{draftId:\\d+}")
//    public DraftSetDto getDraftById(@PathVariable int draftId) {
//        return draftService.findByIdForUser(draftId);
//    }

    @PostMapping("/{draftId:\\d+}/convert")
    public ResponseEntity<SetDto>  createFromDraft(
            @PathVariable int draftId,
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        SetDto set = setService.create(
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId(),
                user,
                payload.terms());
        draftService.delete(draftId, user.getId());
        return ResponseEntity.created(
                uriBuilder.replacePath("/api/sets/{setId}")
                        .build(Map.of("setId", set.id())))
                .body(set);
    }
}
