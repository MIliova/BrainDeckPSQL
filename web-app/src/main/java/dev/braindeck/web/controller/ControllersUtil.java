package dev.braindeck.web.controller;

import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.LanguagesException;
import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.entity.SetExtra;
import dev.braindeck.web.utills.Util;
import lombok.experimental.UtilityClass;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.NoSuchElementException;

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
                model.addAttribute("extra", new SetExtra(ControllersUtil.getLanguageName(languagesList, termLanguageId), ControllersUtil.getLanguageName(languagesList, descriptionLanguageId)));
            }
        } catch (BadRequestException e) {
            throw new LanguagesException(e.getMessage());
        }
    }
    public String getLanguageName (Map<String, Map<Integer, String>> languagesList, Integer languageId) {
        try {
            return languagesList.get("all").get(languageId);
        } catch (NoSuchElementException e) {
            throw new LanguagesException(e.getMessage());
        }
    }

}
