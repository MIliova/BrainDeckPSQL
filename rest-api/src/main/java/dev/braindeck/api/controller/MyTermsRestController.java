package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MyTermsRestController {

    private final TermService termService;
    private final SetService setService;

    @PostMapping("/sets/{setId:\\d+}/terms")
    public TermDto create(@PathVariable("setId") int setId, @RequestBody NewTermPayload term) {
        return termService.create(setService.findEntityById(setId), term);
    }

    @PutMapping("/terms/")
    public ResponseEntity<Void> update(@RequestBody UpdateTermPayload term) {
        termService.update(term);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/terms/{termId:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("termId") int termId) {
        termService.deleteById(termId);
        return ResponseEntity.noContent().build();
    }
}
