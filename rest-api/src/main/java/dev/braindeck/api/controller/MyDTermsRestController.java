package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateDTermPayload;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.service.DraftService;
import dev.braindeck.api.service.DTermService;
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

    @PostMapping
    public TermDto create(
            @PathVariable @Positive(message = "Draft ID must be positive") int draftId,
            @RequestBody NewDTermPayload term) {
        return draftTermService.create(draftService.findEntityById(draftId), term);
    }

    @PostMapping("/batch")
    public List<TermDto> createBatch(
            @PathVariable @Positive(message = "Draft ID must be positive") int draftId,
            @RequestBody List<NewDTermPayload> terms) {
        return draftTermService.create(draftService.findEntityById(draftId), terms);
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable int draftId,
            @PathVariable int termId,
            @RequestBody UpdateDTermPayload payload) {

        draftTermService.update(draftId, termId, payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable int draftId,
            @PathVariable int termId) {

        draftTermService.delete(draftId, termId);
        return ResponseEntity.noContent().build();
    }
}
