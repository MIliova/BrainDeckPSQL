package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.UpdateSetOfTermsPayload;
import dev.braindeck.api.entity.SetOfTerms;
import dev.braindeck.api.service.SetOfTermsService;
import dev.braindeck.api.service.TermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/set/{setId:\\d+}")
public class SetRestController {

    private final SetOfTermsService setOfTermsService;
    private final TermService termService;

    private final MessageSource messageSource;


    @ModelAttribute("set")
    public SetOfTerms getSet(@PathVariable("setId") int setId) {
        return this.setOfTermsService.findSetById(setId);
    }

    @GetMapping
    public SetOfTerms findSet(@ModelAttribute("set") SetOfTerms setOfTerms) {
        return setOfTerms;
    }

    @PatchMapping("/edit")
    public ResponseEntity<Void> updateSet(@PathVariable("setId") int setId,
                                          @Valid @RequestBody UpdateSetOfTermsPayload payload,
                                          BindingResult bindingResult) throws BindException {
        System.out.println(payload);
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.setOfTermsService.updateSet(setId, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), payload.terms());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSet(@PathVariable("setId") int setId) {
        this.setOfTermsService.deleteSet(setId);
        return ResponseEntity.noContent().build();

    }



}
