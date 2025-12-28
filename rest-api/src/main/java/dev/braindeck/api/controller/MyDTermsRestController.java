package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.DTermService;
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
@RequestMapping("/api/me/draft/{draftId:\\d+}/terms")
public class MyDTermsRestController {

    private final DTermService draftTermService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<NewDTermDto> create(
            @PathVariable int draftId,
            @RequestBody @Valid DTermPayload term,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.created(
                        uriBuilder.replacePath("/api/drafts/{draftId}")
                                .build(Map.of("draftId", draftId)))
                .body(draftTermService.create(user, draftId, term));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<NewDTermDto>> createBatch(
            @PathVariable int draftId,
            @RequestBody @Valid List<@Valid DTermPayload> terms,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.created(
                        uriBuilder.replacePath("/api/drafts/{draftId}")
                                .build(Map.of("draftId", draftId)))
                .body(draftTermService.create(user, draftId, terms));
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable @Positive (message = "errors.draft.id") int draftId,
            @PathVariable @Positive (message = "errors.term.id") int termId,
            @RequestBody @Valid DTermPayload payload) {
        UserEntity user = userService.getCurrentUser();
        draftTermService.update(termId, draftId, user.getId(), payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive (message = "errors.draft.id") int draftId,
            @PathVariable @Positive (message = "errors.term.id") int termId) {
        UserEntity user = userService.getCurrentUser();
        draftTermService.delete(termId, draftId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
