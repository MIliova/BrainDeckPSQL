package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.TermService;
import dev.braindeck.api.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/sets/{setId:\\d+}/terms")
public class MyTermsRestController {

    private final TermService termService;
    private final SetService setService;
    private final UserService userService;

    @PostMapping()
    public TermDto create(
            @PathVariable @Positive(message = "errors.set.id") int setId,
            @RequestBody NewDTermPayload term) {
        UserEntity user = userService.getCurrentUser();
        return termService.create(setService.findEntityById(setId, user.getId()), term);
    }

    @PostMapping("/batch")
    public List<TermDto> createBatch(
            @PathVariable @Positive(message = "errors.set.id") int setId,
            @RequestBody List<NewDTermPayload> terms) {
        UserEntity user = userService.getCurrentUser();
        return termService.create(setService.findEntityById(setId, user.getId()), terms);
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable int setId,
            @PathVariable int termId,
            @RequestBody UpdateTermPayload payload) {
        UserEntity user = userService.getCurrentUser();
        termService.update(termId, setId, user.getId(), payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable int setId,
            @PathVariable int termId) {
        UserEntity user = userService.getCurrentUser();
        termService.delete(termId, setId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
