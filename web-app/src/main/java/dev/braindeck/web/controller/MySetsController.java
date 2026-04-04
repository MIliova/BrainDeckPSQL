package dev.braindeck.web.controller;

import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.entity.SetWithCountDto;
import dev.braindeck.web.service.ModelPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MySetsController {

    private final MySetsRestClient mySetsRestClient;
    private final MessageSource messageSource;
    private final ModelPreparationService modelPreparationService;

    @GetMapping("/my/sets")
    public String find(
            Model model, Locale locale) {
        model.addAttribute("currentView", "sets");

        List<SetWithCountDto> sets = mySetsRestClient.findAll();

        modelPreparationService.prepareModel(model, locale, Map.of(
                "sets", sets,
                "pageTitle", messageSource.getMessage("messages.sets", null, locale)
        ));

        return "sets";
    }


}
