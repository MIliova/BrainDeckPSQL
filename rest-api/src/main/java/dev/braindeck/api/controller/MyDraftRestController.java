package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.DraftPayload;
import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/draft")
public class MyDraftRestController {

    private final UserService userService;
    private final DraftService draftService;
    private final SetService setService;

    @GetMapping
    public ResponseEntity<DraftDto> get() {

        UserEntity user = userService.getCurrentUser();
        DraftDto draft = draftService.getDraftDto(user.getId());

        return ResponseEntity.ok(draft);
    }

    @GetMapping("/{draftId:\\d+}")
    public ResponseEntity<DraftDto> getById(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId) {

        UserEntity user = userService.getCurrentUser();
        DraftDto draft = draftService.getDraftDtoById(user.getId(), draftId);

        return ResponseEntity.ok(draft);
    }

    @PatchMapping("/{draftId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId,
            @Valid @RequestBody DraftPayload payload) {
        UserEntity user = userService.getCurrentUser();
        draftService.update(draftId,
                payload.title(), payload.description(),
                payload.termLanguageId(), payload.descriptionLanguageId(),
                user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{draftId:\\d+}")
    public ResponseEntity<DraftDto> deleteAndCreate(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        DraftDto draft = draftService.deleteAndCreate(draftId, user);
        return ResponseEntity.created(
                uriBuilder.replacePath("/api/drafts/{draftId}")
                        .build(Map.of("draftId", draft.id())))
                .body(draft);
    }

    @PostMapping("/{draftId:\\d+}/convert")
    public ResponseEntity<SetDto>  createFromDraft(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId,
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();

        System.out.println(draftId);
        System.out.println(payload);

        SetDto set = setService.createFromDraft(
                draftId,
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId(),
                user.getId());

        return ResponseEntity.created(
                uriBuilder.replacePath("/api/sets/{setId}")
                        .build(Map.of("setId", set.id())))
                .body(set);
    }
}
