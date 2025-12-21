package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.service.ModelPreparationService;
import dev.braindeck.web.service.SetFormResult;
import dev.braindeck.web.service.SetFormService;
import dev.braindeck.web.service.TermParser;
import dev.braindeck.web.utills.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String create (Model model, Locale locale) {
        modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.create.new", null, locale));
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

        SetFormResult result  = setFormService.create(payloadTerms, payload, bindingResult, null);
        if (result.isRedirect()) {
            return "redirect:" + result.getRedirectUrl();
        }

        model.addAllAttributes(result.getModelAttributes());
        modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.create.new", null, locale));
        return "new-set";

    }



}
