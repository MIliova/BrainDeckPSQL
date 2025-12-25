package dev.braindeck.web.service;

import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.UserRestClient;
import dev.braindeck.web.controller.ControllersUtil;
import dev.braindeck.web.domain.LanguageType;
import dev.braindeck.web.entity.LanguagesDto;
import dev.braindeck.web.entity.MyLocale;
import dev.braindeck.web.entity.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Locale;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ModelPreparationService {

    private final LanguagesRestClient languagesRestClient;
    private final MyLocale myLocale;
    private final UserRestClient userRestClient;

    public void prepareModel(Model model, Map<String, Object> modelContext) {
        UserDto user = userRestClient.get();
        model.addAttribute("user", user);

        String curLang = Locale.getDefault().getLanguage(); // можно использовать LocaleContextHolder.getLocale()
        model.addAttribute("curLang", curLang);

        Map<String, String> avLangs = myLocale.getAvailables();
        model.addAttribute("avLangs", avLangs);

        LanguagesDto languagesList = languagesRestClient.findAllByTypes();
        ControllersUtil.getLanguages(languagesList, model);

        modelContext.forEach(model::addAttribute);
    }
}
