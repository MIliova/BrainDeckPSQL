package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyFoldersRestClient;
import dev.braindeck.web.entity.FolderWithCountDto;
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
public class MyFoldersController {

    private final MyFoldersRestClient myFoldersRestClient;
    private final MessageSource messageSource;
    private final ModelPreparationService modelPreparationService;

    @GetMapping("/my/folders")
    public String find(
            Model model, Locale locale) {
        model.addAttribute("currentView", "folders");

        List<FolderWithCountDto> folders = myFoldersRestClient.findAll();

        modelPreparationService.prepareModel(model, locale, Map.of(
                "folders", folders,
                "pageTitle", messageSource.getMessage("messages.folders", null, locale)
        ));

        return "sets";
    }


}
