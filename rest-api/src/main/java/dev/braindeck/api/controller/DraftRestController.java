package dev.braindeck.api.controller;

import dev.braindeck.api.controller.payload.NewSetFromDraftPayload;
import dev.braindeck.api.controller.payload.NewSetPayload;
import dev.braindeck.api.controller.payload.TermsImportPayload.TermsImportPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class DraftRestController {

    private final UserService userService;
    private final DraftSetService draftSetService;
    private final DraftTermService draftTermService;

    private final SetService setService;

    @PostMapping("/terms/prepare-import")
    public List<ImportTermDto> prepareImport(@RequestBody @Valid TermsImportPayload payload, BindingResult bindingResult) throws BindException {

        System.out.println(payload);

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            return this.draftTermService.prepareImport(
                    payload.text(), payload.colSeparator(), payload.rowSeparator(), payload.colCustom(), payload.rowCustom());
        }
    }

    @PostMapping(value = "/terms/create-terms")
    public DraftSetDto createDraftTerms (@RequestBody @Valid NewSetFromDraftPayload payload, BindingResult bindingResult ) throws BindException {
        System.out.println(payload);

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UserEntity user = userService.findById(1);
            return this.draftSetService.create(user, payload);
        }
    }

    @GetMapping("/draft/user/{userId:\\d+}")
    public DraftSetDto getDraftByUserId (@PathVariable("userId") int userId)
    {
        System.out.println("getDraftByUserId1");
        DraftSetDto d= this.draftSetService.findFirstByUserId(userId);
        System.out.println(d);
        System.out.println("getDraftByUserId2");

        return d;
    }


    @GetMapping("/draft/{draftId:\\d+}")
    public DraftSetDto getDraftById (@PathVariable("draftId") int draftId)
    {
        System.out.println("getDraftByUserId1");
        DraftSetDto d= this.draftSetService.findById(draftId);
        System.out.println(d);
        System.out.println("getDraftByUserId2");

        return d;
    }

    @PostMapping("/draft/{draftId:\\d+}/create-set")
    public ResponseEntity<?> createSet(
            @PathVariable("draftId") int draftId,
            @Valid @RequestBody NewSetPayload payload,
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
            UserEntity user = this.userService.findById(1);
            SetDto set = this.setService.createSet(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, payload.terms());
            this.draftSetService.delete(draftId);
            return ResponseEntity.created(uriBuilder
                            .replacePath("/api/sets/{setId}").build(Map.of("setId", set.id())))
                    .body(set);
        }
    }

}
