package dev.braindeck.web.controller;

import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.exception.LanguagesException;
import dev.braindeck.web.entity.SetExtraDto;
import dev.braindeck.web.entity.UserDto;
import lombok.experimental.UtilityClass;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import java.util.*;

@UtilityClass
public class ControllersUtil {

    public void getLanguages(Map<String, Map<Integer, String>> languagesList, Model model) {
        ControllersUtil.getLanguages(languagesList, model, null, null);
    }
    public void getLanguages(Map<String, Map<Integer, String>> languagesList, Model model, Integer termLanguageId, Integer descriptionLanguageId) {
        try {

            model.addAttribute("languages", languagesList.get("rest"));
            model.addAttribute("topLanguages", languagesList.get("top"));
            model.addAttribute("myLanguages", languagesList.get("my"));
            if (termLanguageId != null && descriptionLanguageId != null) {
                model.addAttribute("extra", new SetExtraDto(ControllersUtil.getLanguageName(languagesList, termLanguageId), ControllersUtil.getLanguageName(languagesList, descriptionLanguageId)));
            }
        } catch (BadRequestException e) {
            throw new LanguagesException(e.getMessage());
        }
    }
    public String getLanguageName (Map<String, Map<Integer, String>> languagesList, Integer languageId) {

            List<String> types = new ArrayList<>(languagesList.keySet());

            return types.stream()
                    .map(languagesList::get)
                    .filter(language -> language.containsKey(languageId))
                    .findFirst()
                    .map(map -> map.get(languageId))
                    .orElseThrow(() -> new LanguagesException("errors.language.not_found"));
    }
}
