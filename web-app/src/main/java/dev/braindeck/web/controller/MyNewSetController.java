package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/set")
public class MyNewSetController {

    private final MySetsRestClient mySetsRestClient;
    private final MyDraftRestClient myDraftRestClient;
    private final TermParser termParser;
    private final MessageSource messageSource;

    private final ModelPreparationService modelPreparationService;

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

        if (bindingResult.hasErrors()) {
            modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.create.new", null, locale));
            return "new-set";
        }

        List<NewTermPayload> terms = termParser.parse(payloadTerms);

        System.out.println(terms);

        try {
            SetDto set = mySetsRestClient.create(
                    payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(),
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
}
