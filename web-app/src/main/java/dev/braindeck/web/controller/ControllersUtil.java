package dev.braindeck.web.controller;

import dev.braindeck.web.controller.exception.LanguagesException;
import dev.braindeck.web.domain.LanguageType;
import dev.braindeck.web.entity.LanguagesDto;
import dev.braindeck.web.entity.SetExtraDto;
import lombok.experimental.UtilityClass;
import org.springframework.ui.Model;

import java.util.*;

@UtilityClass
public class ControllersUtil {

    public void getLanguages(LanguagesDto languages, Model model) {
        getLanguages(languages, model, null, null);
    }
    public void getLanguages(LanguagesDto languages, Model model, Integer termLanguageId, Integer descriptionLanguageId) {
        model.addAttribute("languages",
                languages.getOrDefault(LanguageType.REST, Map.of()));
        model.addAttribute("topLanguages",
                languages.getOrDefault(LanguageType.TOP, Map.of()));
        model.addAttribute("myLanguages",
                languages.getOrDefault(LanguageType.MY, Map.of()));
        if (termLanguageId != null && descriptionLanguageId != null) {
            model.addAttribute(
                    "extra",
                    new SetExtraDto(
                            ControllersUtil.getLanguageName(languages, termLanguageId),
                            ControllersUtil.getLanguageName(languages, descriptionLanguageId)));
        }
    }
    public String getLanguageName(
            Map<LanguageType, Map<Integer, String>> languages,
            Integer languageId
    ) {
        return languages.values().stream()
                .map(map -> map.get(languageId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new LanguagesException("errors.language.not_found"));
    }

}
