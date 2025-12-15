package dev.braindeck.web.controller;

import dev.braindeck.web.client.MyTermsRestClient;
import dev.braindeck.web.controller.payload.DTermPayload;
import dev.braindeck.web.entity.NewDTermDto;
import dev.braindeck.web.entity.TermDto;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/me/set/{setId:\\d+}/terms")
public class TermController {

    private final MyTermsRestClient myTermsRestClient;

    @PostMapping()
    public TermDto create(@PathVariable int setId, DTermPayload payload) {
        return myTermsRestClient.create(setId, payload);
    }

    @PostMapping("/batch")
    public List<NewDTermDto> createBatch(
            @PathVariable int draftId,
            @RequestBody List<DTermPayload> terms) {
        return myDTermsRestClient.create(draftId, terms);
    }

    @PutMapping("/{termId:\\d+}")
    public void update(
            @PathVariable @Positive (message = "errors.draft.id") int draftId,
            @PathVariable @Positive (message = "errors.term.id") int termId,
            @RequestBody DTermPayload payload) {
        myDTermsRestClient.update(draftId, termId, payload);
    }

    @DeleteMapping("/{termId:\\d+}")
    public void delete(
            @PathVariable @Positive (message = "errors.draft.id") int draftId,
            @PathVariable @Positive (message = "errors.term.id") int termId) {
        myDTermsRestClient.delete(termId);

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
