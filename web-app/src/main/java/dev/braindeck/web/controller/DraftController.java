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
        modelPreparationService.prepareModel(model, Map.of(
                "payload", new SetFormDto(
                        draft.id(),
                        draft.title(),
                        draft.description(),
                        draft.termLanguageId(),
                        draft.descriptionLanguageId(),
                        draft.terms()
                ),
                "isDraft", true,
                "actionUrl", "/draft",
                "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
        ));
        return "new-set";
    }

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
            modelPreparationService.prepareModel(model, Map.of(
                    "isDraft", true,
                    "actionUrl", "/draft",
                    "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
            ));
            return "new-set";
        }

        TermParser.ParseResult parseResult;

        try {
            parseResult = termParser.parse(payloadTerms);
        } catch (IllegalArgumentException e) {
            modelPreparationService.prepareModel(model, Map.of(
                    "isDraft", true,
                    "actionUrl", "/draft",
                    "errors", Map.of("general", e.getMessage()),
                    "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
            ));
            return "new-set";
        }

        if (parseResult.hasErrors()) {
            modelPreparationService.prepareModel(model, Map.of(
                    "isDraft", true,
                    "actionUrl", "/draft",
                    "terms", parseResult.getTerms(),
                    "termErrors", parseResult.getTermErrors(),
                    "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
            ));
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
            modelPreparationService.prepareModel(model, Map.of(
                    "isDraft", true,
                    "actionUrl", "/draft",
                    "errors", errorDtos,
                    "terms", parseResult.getTerms(),
                    "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
            ));
            return "new-set";
        }
    }

}
