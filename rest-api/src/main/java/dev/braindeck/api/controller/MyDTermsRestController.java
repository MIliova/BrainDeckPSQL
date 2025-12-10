package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateDTermPayload;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.DraftService;
import dev.braindeck.api.service.DTermService;
import dev.braindeck.api.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/drafts/{draftId:\\d+}/terms")
@Validated
public class MyDTermsRestController {

    private final DTermService draftTermService;
    private final DraftService draftService;
    private final UserService userService;


    @PostMapping
    public TermDto create(
            @PathVariable @Positive(message = "errors.draft.id") int draftId,
            @RequestBody NewDTermPayload term) {
        UserEntity user = userService.getCurrentUser();
        return draftTermService.create(draftService.findEntityById(draftId, user.getId()), term);
    }

    @PostMapping("/batch")
    public List<TermDto> createBatch(
            @PathVariable @Positive(message = "errors.draft.id") int draftId,
            @RequestBody List<NewDTermPayload> terms) {
        UserEntity user = userService.getCurrentUser();
        return draftTermService.create(draftService.findEntityById(draftId, user.getId()), terms);
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable int draftId,
            @PathVariable int termId,
            @RequestBody UpdateDTermPayload payload) {
        UserEntity user = userService.getCurrentUser();
        draftTermService.update(termId, draftId, user.getId(), payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable int draftId,
            @PathVariable int termId) {
        UserEntity user = userService.getCurrentUser();
        draftTermService.delete(termId, draftId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
