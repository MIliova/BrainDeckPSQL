package dev.braindeck.web.controller;

import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.MyFoldersRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.FolderEditDto;
import dev.braindeck.web.entity.UserDto;
import dev.braindeck.web.service.*;
import dev.braindeck.web.utills.ProblemDetailParser;
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
@RequestMapping("/folder/{id:\\d+}")
public class MyFolderController {

    private final MyFoldersRestClient myFoldersRestClient;
    private final ModelPreparationService modelPreparationService;
    private final MessageSource messageSource;
    private final LanguagesRestClient languagesRestClient;
    private final LanguagesControllerHelper languagesControllerHelper;

    @GetMapping("/edit")
    public String get(
            @PathVariable("id") int id,
            Model model, Locale locale) {
        model.addAttribute("currentView", "new-folder");

        FolderEditDto folder = myFoldersRestClient.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        languagesControllerHelper.getLanguages(languagesRestClient.findAllByTypes(), model, locale);
        modelPreparationService.prepareModel(model,  locale, Map.of(
                "payload", new FolderPayload(
                        folder.id(),
                        folder.title()
                        ),
                "actionUrl", "/folder/" + id + "/edit",
                "pageTitle", messageSource.getMessage("messages.folder.edit", null, locale)
        ));

        return "new-folder";
    }

    @PostMapping("/edit")
    public String update(
            @PathVariable("id") int id,
            @Valid @ModelAttribute UpdateFolderPayload payload,
            BindingResult bindingResult,
            Model model, Locale locale) {

        model.addAttribute("currentView", "new-folder");

        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);

            languagesControllerHelper.getLanguages(languagesRestClient.findAllByTypes(), model, locale);
            modelPreparationService.prepareModel(model, locale, Map.of(
                    "actionUrl", "/folder/" + id + "/edit",
                    "pageTitle", messageSource.getMessage("messages.folder.edit", null, locale)
            ));

            return "new-folder";
        }

        try {
            myFoldersRestClient.update(id, payload.title());
            return "redirect:/my/folder/" + id;

        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", ProblemDetailParser.parse(e.getRestException()));

            languagesControllerHelper.getLanguages(languagesRestClient.findAllByTypes(), model, locale);
            modelPreparationService.prepareModel(model, locale, Map.of(
                    "actionUrl", "/folder/" + id + "/edit",
                    "pageTitle", messageSource.getMessage("messages.folder.edit", null, locale)
            ));

            return "new-folder";
        }
    }

    @PostMapping("/delete")
    public String delete(@PathVariable("id") int id, Model model) {
        model.addAttribute("currentView", "new-folder");

        myFoldersRestClient.delete(id);
        UserDto userDto = (UserDto) model.getAttribute("user");
        if (userDto == null) {
            return "redirect:/";
        }
        return "redirect:/my/folders";
    }
}
