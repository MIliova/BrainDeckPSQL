package dev.braindeck.web.controller;

import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/{userId:\\d+}/set/{setId:\\d+}")
public class SetController {

    private final SetsRestClient setsRestClient;
    private final ModelPreparationService modelPreparationService;
    private final MessageSource messageSource;

    @GetMapping
    public String get(@PathVariable("setId") int setId,
                          Model model, Locale locale) {
        model.addAttribute("currentView", "set");

        SetShortDto setDto = setsRestClient.findSetById(setId);

        modelPreparationService.prepareModel(model, locale, Map.of(
                "set", setDto,
                "pageTitle", setDto.title()
                )
        );

        return "set";
    }

}
