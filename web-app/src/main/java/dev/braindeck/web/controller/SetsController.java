package dev.braindeck.web.controller;

import dev.braindeck.web.client.*;
import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class SetsController {

    private final SetsRestClient setsRestClient;
    private final MessageSource messageSource;


    @GetMapping("/user/{userId:\\d+}/sets")
    public String find(
            @PathVariable("userId") int userId,
            Model model, Locale locale) {

        List<SetWithCountDto> sets = setsRestClient.findAllSets(userId);
        System.out.println(sets);

        model.addAttribute("sets", sets);
        model.addAttribute("pageTitle", messageSource.getMessage("messages.sets", null, locale));
        return "sets";
    }

}
