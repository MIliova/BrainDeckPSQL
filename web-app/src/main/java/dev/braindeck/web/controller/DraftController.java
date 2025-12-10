package dev.braindeck.web.controller;

import dev.braindeck.web.client.*;
import dev.braindeck.web.controller.payload.NewDraftPayload;
import dev.braindeck.web.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/draft")
public class DraftController {

    private final MyDraftRestClient myDraftRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MessageSource messageSource;

    @PostMapping
    public NewDraftDto create(@Valid @RequestBody NewDraftPayload payload) {
        return myDraftRestClient.create(
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId()
        );
    }

    @PatchMapping("/{draftId:\\d+}")
    public void update(@PathVariable int draftId, NewDraftPayload payload) {
         myDraftRestClient.update(
                draftId,
                payload.title(),
                payload.description(),
                payload.termLanguageId(),
                payload.descriptionLanguageId());
    }

    @DeleteMapping("/{draftId:\\d+}")
    public void delete(@PathVariable int draftId) {
        myDraftRestClient.delete(draftId);
    }

    @GetMapping()
    public String get(@ModelAttribute("draft") DraftSetDto draft, Model model, Locale locale) {
        Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
        ControllersUtil.getLanguages(languagesList, model, draft.termLanguageId(), draft.descriptionLanguageId());
        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
        return "draft-set";
    }

//    @PostMapping("/{draftId:\\d+}/convert")
//    public ResponseEntity<SetDto>  createFromDraft(
//            @PathVariable int draftId,
//            @Valid @RequestBody NewSetPayload payload,
//            UriComponentsBuilder uriBuilder) {
//        UserEntity user = userService.getCurrentUser();
//        SetDto set = setService.create(
//                payload.title(),
//                payload.description(),
//                payload.termLanguageId(),
//                payload.descriptionLanguageId(),
//                user,
//                payload.terms());
//        draftService.delete(draftId, user.getId());
//        return ResponseEntity.created(
//                        uriBuilder.replacePath("/api/sets/{setId}")
//                                .build(Map.of("setId", set.id())))
//                .body(set);
//    }

//    @ModelAttribute("draft")
//    public DraftSetDto findDraftById(@PathVariable("draftId") int draftId, @ModelAttribute("user") UserDto userDto) {
//        DraftSetDto draftSetDto = this.setsRestClient.findDraftById(draftId).orElse(null);
//        System.out.println(draftSetDto);
//        return draftSetDto;
//    }
//





}
