package dev.braindeck.web.service;

import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SetFormService {
    private final ModelPreparationService modelPreparationService;
    private final TermParser termParser;
    private final MessageSource messageSource;
    private final SetCreationService setCreationService;

    public TermsValidateResult validate(
            String payloadTerms
    ) {
        try {
            TermParser.ParseResult parseResult = termParser.parse(payloadTerms);
            if (parseResult.hasErrors()) {
                return TermsValidateResult.termErrors(
                        parseResult.getTerms(),
                        parseResult.getTermErrors());
            }
            return TermsValidateResult.success(parseResult.getTerms());
        } catch (IllegalArgumentException e) {
            return TermsValidateResult.generalError(e.getMessage());
        }
    }

    public SetFormResult create(
            NewSetPayload payload,
            List<NewTermPayload> terms,
            Integer draftId
    ) {

        SetCreationResult creationResult =
                setCreationService.createSet(payload, terms, draftId);

        if (creationResult.hasErrors()) {
            return SetFormResult.error(Map.of(
                    "errors", creationResult.getErrors(),
                    "payload", payload,
                    "terms", terms
            ));
        }

        SetDto set = creationResult.getSet();
        return SetFormResult.redirect("/set/" + set.id());

    }

}

