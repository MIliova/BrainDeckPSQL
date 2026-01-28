package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.TermService;
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
@RequestMapping("/api/me/sets/{setId:\\d+}/terms")
public class MyTermsRestController {

    private final TermService termService;
    private final UserService userService;

//    @PostMapping("/autosave")
//    public ResponseEntity<NewDTermDto> autoCreate(
//            @PathVariable("setId") int setId,
//            @RequestBody @Valid DTermPayload term,
//            UriComponentsBuilder uriBuilder) {
//        UserEntity user = userService.getCurrentUser();
//        return ResponseEntity.created(
//                uriBuilder.replacePath("/api/me/sets/{setId:\\d+}/terms/autosave")
//                        .build(Map.of("setId", setId)))
//                .body(termService.autoCreate(user.getId(), setId, term));
//    }
//
//    @PatchMapping("/{termId:\\d+}/autosave")
//    public ResponseEntity<Void> autoUpdate(
//            @PathVariable("setId") @Positive (message = "errors.set.id") int setId,
//            @PathVariable("termId") @Positive (message = "errors.term.id") int termId,
//            @RequestBody @Valid DTermPayload payload) {
//        UserEntity user = userService.getCurrentUser();
//        termService.autoUpdate(user.getId(), setId, termId, payload);
//        return ResponseEntity.noContent().build();
//    }






    @PostMapping
    public ResponseEntity<TermDto> create(
            @PathVariable("setId") @Positive(message = "errors.set.id") int setId,
            @RequestBody @Valid DTermPayload term,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/me/sets/{setId}").build(Map.of("setId", setId)))
                .body(termService.create(user.getId(), setId, term));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<TermDto>> createBatch(
            @PathVariable("setId") @Positive(message = "errors.set.id") int setId,
            @RequestBody @Valid List<@Valid DTermPayload> terms,
            UriComponentsBuilder uriBuilder) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/me/sets/{setId}").build(Map.of("setId", setId)))
                .body(termService.create(user.getId(), setId, terms));
    }

    @PutMapping("/{termId:\\d+}")
    public ResponseEntity<Void> update(
            @PathVariable("setId") @Positive int setId,
            @PathVariable("termId") @Positive int termId,
            @RequestBody @Valid UpdateTermPayload payload) {
        UserEntity user = userService.getCurrentUser();
        termService.update(termId, setId, user.getId(), payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{termId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable("setId") @Positive int setId,
            @PathVariable("termId") @Positive int termId) {
        UserEntity user = userService.getCurrentUser();
        termService.delete(termId, setId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
