package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MyDraftTermsRestClient;
import dev.braindeck.web.controller.payload.NewDraftPayload;
import dev.braindeck.web.entity.NewDraftDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/me/draft/{draftId:\\d+}")
public class DTermController {

    private final MyDraftTermsRestClient myDraftTermsRestClient;

    @PostMapping()
    public NewDraftDto create(NewDraftPayload payload) {
        return myDraftsRestClient.create(
                payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId());
    }

//    @ModelAttribute("draft")
//    public DraftSetDto findDraftById(@PathVariable("draftId") int draftId, @ModelAttribute("user") UserDto userDto) {
//        DraftSetDto draftSetDto = this.setsRestClient.findDraftById(draftId).orElse(null);
//        System.out.println(draftSetDto);
//        return draftSetDto;
//    }
//
//    @GetMapping()
//    public String getDraftPage(@ModelAttribute("draft") DraftSetDto draft, Model model, Locale locale) {
//
//        Map<String, Map<Integer, String>> languagesList = this.languagesRestClient.findAllByTypes();
//        System.out.println(languagesList);
//        ControllersUtil.getLanguages(languagesList, model, draft.termLanguageId(), draft.descriptionLanguageId());
//
//        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
//
//        return "draft-set";
//    }



}
