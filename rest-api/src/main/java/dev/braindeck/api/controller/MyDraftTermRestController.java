package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewTermWithSetIdPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drafts/{setId:\\d+}/terms")
public class MyDraftTermRestController {

    private final TermService termService;
    private final SetService setService;

    @PostMapping
    public TermDto create(@PathVariable("setId") int setId, @RequestBody NewTermWithSetIdPayload term) {
        return termService.create(setService.findEntityById(setId), term);
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(@RequestBody UpdateTermPayload term) {
        termService.update(term);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("termId") int termId) {
        termService.deleteById(termId);
        return ResponseEntity.noContent().build();
    }
}
