package dev.braindeck.web.controller;

import dev.braindeck.web.controller.exception.LanguagesException;
import dev.braindeck.web.domain.LanguageType;
import dev.braindeck.web.entity.LanguagesDto;
import dev.braindeck.web.entity.SetExtraDto;
import lombok.experimental.UtilityClass;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Stream;

@UtilityClass
public class ControllersUtil {

    public void getLanguages(LanguagesDto languages, Model model) {
        getLanguages(languages, model, null, null);
    }
    public void getLanguages(LanguagesDto languages, Model model, Integer termLanguageId, Integer descriptionLanguageId) {
        model.addAttribute("languages", languages.rest());
        model.addAttribute("topLanguages", languages.top());
        model.addAttribute("myLanguages", languages.my());
        if (termLanguageId != null && descriptionLanguageId != null) {
            model.addAttribute(
                    "extra",
                    new SetExtraDto(
                            ControllersUtil.getLanguageName(languages, termLanguageId),
                            ControllersUtil.getLanguageName(languages, descriptionLanguageId)));
        }
    }
    public String getLanguageName(
            LanguagesDto languages,
            Integer languageId
    ) {
        return Stream.of(languages.top(), languages.rest(), languages.my())
                .map(map -> map.get(languageId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new LanguagesException("errors.language.not_found"));
    }

}
