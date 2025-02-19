package dev.braindeck.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.entity.MyLocale;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.SetWithCountDto;
import dev.braindeck.web.entity.UserDto;
import dev.braindeck.web.utills.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SetsController {

    private final SetsRestClient setsRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MyLocale myLocale;


    @ModelAttribute("user")
    public UserDto findCurrentUser() {
        return this.setsRestClient.findCurrentUser();
    }

    @ModelAttribute("curLang")
    public String getCurLang() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    @ModelAttribute("avLangs")
    public Map<String, String> getAvLangs() {
        return this.myLocale.getAvailables();
    }

    @GetMapping("/user/{userId:\\d+}/sets")
    public String getSetsList(@PathVariable("userId") int userId, Model model) {
//        model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//        model.addAttribute("avLangs", this.myLocale.getAvailables());


        List<SetWithCountDto> sets = this.setsRestClient.findAllSets(userId);
        System.out.println(sets);
        model.addAttribute("sets", sets);
        return "sets";
    }

    @GetMapping("/create-set")
    public String getNewSetPage (Model model) {
//        model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//        model.addAttribute("avLangs", this.myLocale.getAvailables());

        Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
        System.out.println("getNewSetPage languagesRestClient.findAllByTypes");

        ControllersUtil.getLanguages(languagesList, model);

        return "new-set";
    }
    @PostMapping(value = "/create-set")
    public String createSet(
            @RequestParam("terms") String payloadTerms,
            NewSetPayload payload,
                            Model model) {
        System.out.println(payload);
        System.out.println(payloadTerms);
        ObjectMapper objectMapper = new ObjectMapper();
        List<NewTermPayload> terms = null;
        try {
            terms = objectMapper.readValue(payloadTerms, new TypeReference<List<NewTermPayload>>(){});
        } catch (IOException e) {
            throw new JsonParseException();
        }
        System.out.println(terms);

        try {
            SetDto set = this.setsRestClient.createSet(
                    payload.title(), payload.description(), payload.termLanguageId(),payload.descriptionLanguageId(),
                    terms);
            return "redirect:/set/" + set.id();
        } catch(BadRequestException e) {

            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
            model.addAttribute("errors", errorDtos);

            model.addAttribute("payload", payload);
            if (terms != null) {
                model.addAttribute("terms", terms);
            }
//            model.addAttribute("curLang", LocaleContextHolder.getLocale().getLanguage());
//            model.addAttribute("avLangs", this.myLocale.getAvailables());

            Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
            ControllersUtil.getLanguages(languagesList, model);

            return "new-set";
        }
    }
}
