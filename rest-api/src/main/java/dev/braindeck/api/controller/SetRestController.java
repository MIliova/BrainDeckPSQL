package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.UpdateSetPayload;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.service.SetService;
import dev.braindeck.api.service.TermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/set/{setId:\\d+}")
public class SetRestController {

    private final SetService setService;
    private final TermService termService;

    private final MessageSource messageSource;


    @ModelAttribute("set")
    public Set getSet(@PathVariable("setId") int setId) {
        return this.setService.findSetById(setId);
    }

    @GetMapping
    public Set findSet(@ModelAttribute("set") Set set) {
        return set;
    }

    @PatchMapping("/edit")
    public ResponseEntity<Void> updateSet(@PathVariable("setId") int setId,
                                          @Valid @RequestBody UpdateSetPayload payload,
                                          BindingResult bindingResult) throws BindException {
        System.out.println(payload);
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.setService.updateSet(setId, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), payload.terms());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSet(@PathVariable("setId") int setId) {
        this.setService.deleteSet(setId);
        return ResponseEntity.noContent().build();

    }



}
