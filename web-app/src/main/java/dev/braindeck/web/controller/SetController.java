package dev.braindeck.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.LanguagesException;
import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.payload.UpdateSetOfTermsPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.utills.Util;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/set/{setId:\\d+}")
public class SetController {

    private final SetsRestClient setsRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MessageSource messageSource;
    private final MyLocale myLocale;

    @ModelAttribute("set")
    public SetOfTerms getSet(@PathVariable("setId") int setId) {
        return this.setsRestClient.findSetById(setId).orElse(null);
    }

    @ModelAttribute("curLang")
    public String getCurLang() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    @ModelAttribute("avLangs")
    public Map<String, String> getAvLangs() {
        return this.myLocale.getAvailables();
    }

    @GetMapping
    public String findSet(@PathVariable("setId") int setId, Model model) {
//        model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//        model.addAttribute("avLangs", this.myLocale.getAvailables());
        return "set";
    }

    @GetMapping("/edit")
    public String getEditSetPage(@PathVariable("setId") int setId, @ModelAttribute("set") SetOfTerms setOfTerms, Model model) {
//        model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//        model.addAttribute("avLangs", this.myLocale.getAvailables());

        Map<String, Map<Integer, String>> languagesList = languagesRestClient.getAll();
        ControllersUtil.getLanguages(languagesList, model, setOfTerms.termLanguageId(),setOfTerms.descriptionLanguageId());

        return "edit-set";
    }

    @PostMapping("/edit")
    public String updateSet(@ModelAttribute(value = "setId", binding = false) int setId,
                            @RequestParam("terms") String payloadTerms,
                            UpdateSetOfTermsPayload payload,
                            Model model) {
        System.out.println(payload);
        System.out.println(payloadTerms);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Term> terms = null;
        try {
            terms = objectMapper.readValue(payloadTerms, new TypeReference<List<Term>>(){});
        } catch (IOException e) {
            throw new JsonParseException();
        }
//        System.out.println(terms);

        try {
            this.setsRestClient.updateSet(setId, payload.title(), payload.description(), payload.termLanguageId(),
                    payload.descriptionLanguageId(), terms);
            return "redirect:/set/" + setId;
        } catch (BadRequestException e) {
            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
            model.addAttribute("errors", errorDtos);

//            model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//            model.addAttribute("avLangs", this.myLocale.getAvailables());

            model.addAttribute("payload", payload);
            if (terms != null) {
                model.addAttribute("terms", terms);
            }

            Map<String, Map<Integer, String>> languagesList = languagesRestClient.getAll();
            ControllersUtil.getLanguages(languagesList, model, payload.termLanguageId(),payload.descriptionLanguageId());

            return "edit-set";
        }
    }

    @PostMapping("/delete")
    public String deleteSet(@ModelAttribute("setId") int setId) {
        this.setsRestClient.deleteSet(setId);
        return "redirect:/user/sets";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e,
                                               Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        return "error/404";
    }

}
