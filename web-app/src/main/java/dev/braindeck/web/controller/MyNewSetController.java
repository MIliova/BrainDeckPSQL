package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.NewSetFormDto;
import dev.braindeck.web.service.*;
import jakarta.validation.Valid;
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
@RequestMapping("/set")
public class MyNewSetController {

    private final MySetsRestClient mySetsRestClient;
    private final MyDraftRestClient myDraftRestClient;
    private final TermParser termParser;
    private final MessageSource messageSource;

    private final ModelPreparationService modelPreparationService;
    private final SetFormService setFormService;

    @GetMapping
    public String create(Model model, Locale locale) {
        NewSetFormDto payload = new NewSetFormDto();

        modelPreparationService.prepareModel(model, Map.of(
                "actionUrl", "/set",
                "payload", payload,
                "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
        ));
        return "new-set";
    }

    @PostMapping
    public String create(
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
                    "actionUrl", "/set",
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
                "actionUrl", "/set",
                "pageTitle", messageSource.getMessage("messages.set.create.new", null, locale)
        ));
        return "new-set";
    }

}
