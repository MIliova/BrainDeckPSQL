package dev.braindeck.web.controller;

import dev.braindeck.web.client.*;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.DraftPayload;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
import dev.braindeck.web.service.TermParser;
import dev.braindeck.web.utills.Util;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/draft")
public class DraftController {

    private final MyDraftRestClient myDraftRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MessageSource messageSource;
    private final ModelPreparationService modelPreparationService;
    private final TermParser termParser;
    private final MySetsRestClient mySetsRestClient;

    @GetMapping
    public String get(Model model, Locale locale) {
        DraftDto draft = myDraftRestClient.get().orElse(null);
        if (draft == null) {
            return "redirect:/set";
        }
        model.addAttribute("draft", draft);
        LanguagesDto languagesList = languagesRestClient.findAllByTypes();
        ControllersUtil.getLanguages(languagesList, model, draft.termLanguageId(), draft.descriptionLanguageId());
        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create.new", null, locale));
        return "new-set";
    }

//    @PostMapping
//    public NewDraftDto create(@Valid @RequestBody DraftPayload payload) {
//        return myDraftRestClient.create(
//                payload.title(),
//                payload.description(),
//                payload.termLanguageId(),
//                payload.descriptionLanguageId()
//        );
//    }

//    @PatchMapping("/{draftId:\\d+}")
//    @ResponseBody
//    public Map<String, Object> update(@PathVariable @Positive(message = "errors.draft.id") int draftId, @RequestBody DraftPayload payload) {
//        try {
//            myDraftRestClient.update(
//                    draftId,
//                    payload.title(),
//                    payload.description(),
//                    payload.termLanguageId(),
//                    payload.descriptionLanguageId()
//            );
//            return Map.of("status", "success");
//        } catch (BadRequestException e) {
//            List<FieldErrorDto> errors = Util.problemDetailErrorToDtoList(e.getJsonObject());
//            return Map.of(
//                    "status", "error",
//                    "errors", errors
//            );
//        }
//    }

//    @DeleteMapping("/{draftId:\\d+}")
//    @ResponseBody
//    public String delete(@PathVariable @Positive(message = "errors.draft.id") int draftId) {
//        myDraftRestClient.delete(draftId);
//        return "redirect:/draft";
//    }

    @PostMapping("/{draftId:\\d+}")
    public String create(
            @PathVariable @Positive(message = "errors.draft.id") int draftId,
            @RequestParam("terms") String payloadTerms,
            @Valid @ModelAttribute("payload") NewSetPayload payload,
            BindingResult bindingResult,
            Model model,
            Locale locale
    ) {

        if (bindingResult.hasErrors()) {
            modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.create.new", null, locale));
            return "new-set";
        }

        TermParser.ParseResult parseResult;

        try {
            parseResult = termParser.parse(payloadTerms);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errors", Map.of("general", e.getMessage()));
            return "new-set";
        }

        if (parseResult.hasErrors()) {
            model.addAttribute("termErrors", parseResult.getTermErrors());
            model.addAttribute("terms", parseResult.getTerms());
            return "new-set";
        }

        List<NewTermPayload> terms = parseResult.getTerms();

        try {
            SetDto set = mySetsRestClient.create(
                    payload.title(),
                    payload.description(),
                    payload.termLanguageId(),
                    payload.descriptionLanguageId(),
                    terms);
            return "redirect:/set/" + set.id();
        } catch(BadRequestException e) {

            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
            model.addAttribute("errors", errorDtos);

            model.addAttribute("payload", payload);
            model.addAttribute("terms", terms);

            modelPreparationService.prepareModel(model, locale, "messages.set.create.new");
            return "new-set";
        }
    }


//    @ModelAttribute("draft")
//    public DraftSetDto findDraftById(@PathVariable("draftId") int draftId, @ModelAttribute("user") UserDto userDto) {
//        DraftSetDto draftSetDto = this.setsRestClient.findDraftById(draftId).orElse(null);
//        System.out.println(draftSetDto);
//        return draftSetDto;
//    }
//





}
