package dev.braindeck.web.controller;

import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.UpdateSetPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
import dev.braindeck.web.service.TermParser;
import dev.braindeck.web.utills.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/set/{setId:\\d+}")
public class MySetController {

    private final MySetsRestClient mySetsRestClient;
    private final TermParser termParser;
    private final ModelPreparationService modelPreparationService;
    private final MessageSource messageSource;


    @GetMapping("/edit")
    public String edit(
            @PathVariable int setId,
            Model model, Locale locale) {
        SetDto setDto = mySetsRestClient.findMySetById(setId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        System.out.println(setDto);
        model.addAttribute("set", setDto);

        modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.edit", null, locale));

        return "edit-set";
    }

    @PostMapping("/edit")
    public String update(
            @PathVariable int setId,
            @RequestParam("terms") String payloadTerms,
            @Valid @ModelAttribute UpdateSetPayload payload,
            BindingResult bindingResult,
            Model model, Locale locale) {

        System.out.println(payload);
        System.out.println(payloadTerms);

        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.edit", null, locale));
            return "edit-set";
        }

        List<UpdateTermPayload> terms = termParser.parseForUpdate(payloadTerms);

//        System.out.println(terms);

        try {
            mySetsRestClient.update(setId, payload.title(), payload.description(), payload.termLanguageId(),
                    payload.descriptionLanguageId(), terms);
            return "redirect:/set/" + setId;
        } catch (BadRequestException e) {
            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
            model.addAttribute("errors", errorDtos);

            model.addAttribute("payload", payload);
            model.addAttribute("terms", terms);

            modelPreparationService.prepareModel(model, locale, messageSource.getMessage("messages.set.edit", null, locale));

            return "edit-set";
        }
    }

    @PostMapping("/delete")
    public String delete(@PathVariable int setId, Model model) {
        mySetsRestClient.delete(setId);
        UserDto userDto = (UserDto) model.getAttribute("user");
        if (userDto == null) {
            return "redirect:/"; // Или другая страница, например, главная
        }
        return "redirect:/user/"+userDto.id()+"/sets";
    }
}
