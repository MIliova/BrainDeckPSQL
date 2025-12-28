package dev.braindeck.web.controller;

import dev.braindeck.web.client.*;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SetsController {

    private final SetsRestClient setsRestClient;
    private final MessageSource messageSource;
    private final ModelPreparationService modelPreparationService;

    @GetMapping("/user/{userId:\\d+}/sets")
    public String find(
            @PathVariable("userId") int userId,
            Model model, Locale locale) {
        model.addAttribute("currentView", "setы");

        List<SetWithCountDto> sets = setsRestClient.findAllSets(userId);
        modelPreparationService.prepareModel(model, locale, Map.of(
                "sets", sets,
                "pageTitle", messageSource.getMessage("messages.sets", null, locale)
        ));

        return "sets";
    }

}
