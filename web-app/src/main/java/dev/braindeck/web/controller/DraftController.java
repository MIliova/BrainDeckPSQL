package dev.braindeck.web.controller;

import dev.braindeck.web.client.*;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final SetFormService setFormService;

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
                "actionUrl", "/draft/" + draft.id(),
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

        TermsValidateResult<NewTermPayload> termsValidateResult  = setFormService.validate(payloadTerms, NewTermPayload.class);
        if (bindingResult.hasErrors() || termsValidateResult.hasErrors()) {
            model.addAllAttributes(termsValidateResult.getModelAttributes());
            modelPreparationService.prepareModel(model, Map.of(
                    "isDraft", true,
                    "actionUrl", "/draft/" + draftId,
                    "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
            ));
            return "new-set";
        }

        SetFormResult result = setFormService.create(payload, termsValidateResult.getTerms(), null);
        if (result.isRedirect()) {
            return "redirect:" + result.getRedirectUrl();
        }
        model.addAllAttributes(result.getModelAttributes());
        modelPreparationService.prepareModel(model, Map.of(
                "isDraft", true,
                "actionUrl", "/draft/" + draftId,
                "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
        ));
        return "new-set";
    }

}
