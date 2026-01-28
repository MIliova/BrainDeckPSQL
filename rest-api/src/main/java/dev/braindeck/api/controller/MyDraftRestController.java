package dev.braindeck.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
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

    @PostMapping("/{draftId:\\d+}/convert")
    public ResponseEntity<SetDto>  createSetFromDraft(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId,
            @Valid @RequestBody NewSetPayload payload,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();

        SetDto set = setService.createFromDraft(
                user.getId(),
                draftId,
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId()
                );

        return ResponseEntity.created(
                        uriBuilder.replacePath("/api/sets/{setId}")
                                .build(Map.of("setId", set.id())))
                .body(set);
    }

    @PatchMapping("/{draftId:\\d+}")
    public ResponseEntity<Void> autoUpdate(
            @PathVariable("draftId") @Positive (message = "errors.draft.id") int draftId,
            @RequestBody JsonNode body) {

        UserEntity user = userService.getCurrentUser();
        draftService.autoUpdate(
                user.getId(),
                draftId,
                body);
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


}
