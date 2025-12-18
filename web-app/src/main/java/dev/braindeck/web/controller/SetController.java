package dev.braindeck.web.controller;

import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.service.ModelPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/{userId:\\d+}/set/{setId:\\d+}")
public class SetController {

    private final SetsRestClient setsRestClient;
    private final ModelPreparationService modelPreparationService;

    @GetMapping
    public String get(@PathVariable("setId") int setId,
                          Model model, Locale locale) {
        SetDto setDto = setsRestClient.findSetById(setId).orElse(null);
        System.out.println(setDto);
        model.addAttribute("set", setDto);

        modelPreparationService.prepareModel(model, locale, setDto != null ? setDto.title() :"");

        return "set";
    }

}
