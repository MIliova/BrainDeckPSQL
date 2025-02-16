package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.service.SetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SetsRestController {

    private final SetService setService;

    @GetMapping("/user/{userId:\\d+}/sets")
    public List<Set> findSets(@PathVariable("userId") int userId) {
        return this.setService.findAllByUserId(userId);
    }

    @PostMapping("/user/{userId:\\d+}/sets/create")
    public ResponseEntity<?> createSet(@PathVariable("userId") int userId, @Valid @RequestBody NewSetPayload payload,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriBuilder) throws BindException {
        System.out.println(payload);
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Set set = this.setService.createSet(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), userId, payload.terms());
            return ResponseEntity.created(uriBuilder
                    .replacePath("/api/sets/{setId}").build(Map.of("setId", set.getId())))
                    .body(set);
        }
    }
}
