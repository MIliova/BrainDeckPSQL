package dev.braindeck.web.controller;

import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.payload.DraftPayload;
import dev.braindeck.web.controller.payload.UpdateSetPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/set/{setId:\\d+}")
public class MySetController {

    private final MySetsRestClient mySetsRestClient;
    private final ModelPreparationService modelPreparationService;
    private final MessageSource messageSource;
    private final SetFormService setFormService;

    @GetMapping("/edit")
    public String get(
            @PathVariable("setId") int setId,
            Model model, Locale locale) {
        model.addAttribute("currentView", "edit-set");

        SetFullDto set = mySetsRestClient.findMySetById(setId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        modelPreparationService.prepareModel(model,  locale, Map.of(
                "payload", new DraftPayload(
                        set.id(),
                        set.title(),
                        set.description(),
                        set.termLanguageId(),
                        set.descriptionLanguageId()
                        ),
                "terms", set.terms(),
                "actionUrl", "/set/" + setId + "/edit",
                "pageTitle", messageSource.getMessage("messages.set.edit", null, locale)
        ));

        return "edit-set";
    }

    @PostMapping("/edit")
    public String update(
            @PathVariable("setId") int setId,
            @RequestParam("terms") String payloadTerms,
            @Valid @ModelAttribute UpdateSetPayload payload,
            BindingResult bindingResult,
            Model model, Locale locale) {

        model.addAttribute("currentView", "edit-set");

        TermsValidateResult<UpdateTermPayload> termsValidateResult  = setFormService.validate(payloadTerms, UpdateTermPayload.class);
        if (bindingResult.hasErrors() || termsValidateResult.hasErrors()) {
            model.addAllAttributes(termsValidateResult.getModelAttributes());
            modelPreparationService.prepareModel(model, locale, Map.of(
                    "actionUrl", "/set/" + setId + "/edit",
                    "pageTitle", messageSource.getMessage("messages.set.edit", null, locale)
            ));
            return "edit-set";
        }

        SetFormResult result = setFormService.update(setId, payload, termsValidateResult.getTerms());
        if (result.isRedirect()) {
            return "redirect:" + result.getRedirectUrl();
        }
        model.addAllAttributes(result.getModelAttributes());
        modelPreparationService.prepareModel(model, locale, Map.of(
                "actionUrl", "/set/" + setId + "/edit",
                "pageTitle", messageSource.getMessage("messages.set.edit", null, locale)
        ));
        return "edit-set";
    }

    @PostMapping("/delete")
    public String delete(@PathVariable("setId") int setId, Model model) {
        model.addAttribute("currentView", "edit-set");

        mySetsRestClient.delete(setId);
        UserDto userDto = (UserDto) model.getAttribute("user");
        if (userDto == null) {
            return "redirect:/";
        }
        return "redirect:/user/"+userDto.id()+"/sets";
    }
}
