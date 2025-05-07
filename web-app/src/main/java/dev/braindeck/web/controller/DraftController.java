package dev.braindeck.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.braindeck.web.client.BadRequestException;
import dev.braindeck.web.client.LanguagesRestClient;
import dev.braindeck.web.client.SetsRestClient;
import dev.braindeck.web.controller.payload.NewSetFromDraftPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;
import dev.braindeck.web.utills.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/draft/{draftId:\\d+}")
public class DraftController {

    private final SetsRestClient setsRestClient;
    private final LanguagesRestClient languagesRestClient;
    private final MyLocale myLocale;
    private final MessageSource messageSource;

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

    @ModelAttribute("draft")
    public DraftSetDto findDraftById(@PathVariable("draftId") int draftId, @ModelAttribute("user") UserDto userDto) {
        DraftSetDto draftSetDto = this.setsRestClient.findDraftById(draftId).orElse(null);
        System.out.println(draftSetDto);
        return draftSetDto;
    }

    @GetMapping()
    public String getDraftPage(@ModelAttribute("draft") DraftSetDto draft, Model model, Locale locale) {

        Map<String, Map<Integer, String>> languagesList = this.languagesRestClient.findAllByTypes();
        System.out.println(languagesList);
        ControllersUtil.getLanguages(languagesList, model, draft.termLanguageId(), draft.descriptionLanguageId());

        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));

        return "draft-set";
    }

    @PostMapping()
    public String createSet(
            @ModelAttribute(value = "draftId", binding = false) int draftId,
            @RequestParam("terms") String payloadTerms,
            NewSetFromDraftPayload payload,
            Model model, Locale locale) {

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
            SetDto set = this.setsRestClient.createSetFromDraft(
                    draftId, payload.title(), payload.description(), payload.termLanguageId(),payload.descriptionLanguageId(),
                    terms);

            return "redirect:/set/" + set.id();
        } catch(BadRequestException e) {

            List<FieldErrorDto> errorDtos = Util.problemDetailErrorToDtoList(e.getJsonObject());
            model.addAttribute("errors", errorDtos);
            model.addAttribute("payload", payload);

            if (terms != null) {
                model.addAttribute("terms", terms);
            }

            Map<String, Map<Integer, String>> languagesList = languagesRestClient.findAllByTypes();
            ControllersUtil.getLanguages(languagesList, model);

            model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
            return "draft-set";
        }
    }

}
