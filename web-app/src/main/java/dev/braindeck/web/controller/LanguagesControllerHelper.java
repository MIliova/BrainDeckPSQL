package dev.braindeck.web.controller;

import dev.braindeck.web.entity.LanguagesDto;
import dev.braindeck.web.entity.SetExtraDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LanguagesControllerHelper {

    private final MessageSource messageSource;


    public void getLanguages(LanguagesDto languages, Model model, Locale locale) {
        getLanguages(languages, model, locale, null, null);
    }
    public void getLanguages(LanguagesDto languages, Model model, Locale locale, Integer termLanguageId, Integer descriptionLanguageId) {
        model.addAttribute("languages", languages.rest());
        model.addAttribute("topLanguages", languages.top());
        model.addAttribute("myLanguages", languages.my());
        if (termLanguageId != null && descriptionLanguageId != null) {
            model.addAttribute("extra", new SetExtraDto(
                            getLanguageName(languages, termLanguageId, locale),
                            getLanguageName(languages, descriptionLanguageId, locale)));
        }
    }
    public String getLanguageName(LanguagesDto languages, Integer languageId, Locale locale) {

        return Stream.of(languages.top(), languages.rest(), languages.my())
                .map(map -> map.get(languageId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> messageSource.getMessage("errors.language.not_found", null, locale
        ));

    }

}
