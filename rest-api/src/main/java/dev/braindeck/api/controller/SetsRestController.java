package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewSetOfTermsPayload;
import dev.braindeck.api.entity.SetOfTerms;
import dev.braindeck.api.service.SetOfTermsService;
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
@RequestMapping("/api/sets")
public class SetsRestController {

    private final SetOfTermsService setOfTermsService;

    @GetMapping
    public List<SetOfTerms> findSets() {
        return this.setOfTermsService.findAllSets();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSet(@Valid @RequestBody NewSetOfTermsPayload payload,
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
            SetOfTerms setOfTerms = this.setOfTermsService.createSet(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), payload.terms());
            return ResponseEntity.created(uriBuilder
                    .replacePath("/api/sets/{setId}").build(Map.of("setId", setOfTerms.getId())))
                    .body(setOfTerms);
        }
    }
}
