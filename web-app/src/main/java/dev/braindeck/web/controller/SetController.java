package dev.braindeck.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.payload.UpdateSetPayload;
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


@Controller
@RequiredArgsConstructor
@RequestMapping("/set/{setId:\\d+}")
public class SetController {

    private final SetsRestClient setsRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MessageSource messageSource;
    private final MyLocale myLocale;

    @ModelAttribute("curLang")
    public String getCurLang() {
        return LocaleContextHolder.getLocale().getLanguage();
    }
    @ModelAttribute("avLangs")
    public Map<String, String> getAvLangs() {
        return this.myLocale.getAvailables();
    }



    @ModelAttribute("user")
    public UserDto findCurrentUser() {
        UserDto userDto=this.setsRestClient.findCurrentUser();
        System.out.println(userDto);
        return userDto;
    }

    @ModelAttribute("set")
    public SetDto getSet(@PathVariable("setId") int setId) {
        SetDto setDto = this.setsRestClient.findSetById(setId).orElse(null);
        System.out.println(setDto);
        return setDto;
    }


    @GetMapping
    public String findSet(@PathVariable("setId") int setId, Model model) {
        SetDto set = (SetDto) model.getAttribute("set");

        if (set != null) {
            model.addAttribute("pageTitle", set.title());
        } else {
            model.addAttribute("pageTitle", "");
        }
        return "set";
    }

    @PostMapping("/delete")
    public String deleteSet(@ModelAttribute("setId") int setId, Model model) {
        this.setsRestClient.deleteSet(setId);
        UserDto userDto = (UserDto) model.getAttribute("user");
        if (userDto == null) {
            return "redirect:/"; // Или другая страница, например, главная
        }
        return "redirect:/user/"+userDto.id()+"/sets";
    }

    @GetMapping("/edit")
    public String getEditSetPage(@PathVariable("setId") int setId, @ModelAttribute("set") SetDto set,
                                 Model model, Locale locale) {

        Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
        ControllersUtil.getLanguages(languagesList, model, set.termLanguageId(), set.descriptionLanguageId());

        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.edit", null, locale));
        return "edit-set";
    }

    @PostMapping("/edit")
    public String updateSet(@ModelAttribute(value = "setId", binding = false) int setId,
                            @RequestParam("terms") String payloadTerms,
                            UpdateSetPayload payload,
                            Model model, Locale locale) {
        System.out.println(payload);
        System.out.println(payloadTerms);
        ObjectMapper objectMapper = new ObjectMapper();
        List<TermDto> terms = null;
        try {
            terms = objectMapper.readValue(payloadTerms, new TypeReference<List<TermDto>>(){});
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

            Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
            ControllersUtil.getLanguages(languagesList, model, payload.termLanguageId(),payload.descriptionLanguageId());

            model.addAttribute("pageTitle", messageSource.getMessage("messages.set.edit", null, locale));
            return "edit-set";
        }
    }



    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e,
                                               Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
        return "error/404";
    }

}
