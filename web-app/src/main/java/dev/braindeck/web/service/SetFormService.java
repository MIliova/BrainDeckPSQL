package dev.braindeck.web.service;

import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.SetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetFormService {
    private final TermParser termParser;
    private final SetService setService;

    public <T> TermsValidateResult<T> validate(String payloadTerms, Class<T> clazz) {
        try {
            TermParser.ParseResult<T> parseResult = termParser.parse(payloadTerms, clazz);
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
            NewSetPayloadC payload,
            List<NewTermPayload> terms,
            Integer draftId
    ) {
        SetCreationResult result = setService.create(payload, terms, draftId);

        if (result.hasErrors()) {
            return SetFormResult.error(
                    result.getErrors(),
                    payload,
                    terms);
        }

        SetDto set = result.getSet();
        return SetFormResult.redirect("/set/" + set.id());
    }


    public SetFormResult update(
            Integer setId,
            UpdateSetPayload payload,
            List<UpdateTermPayload> terms
    ) {
        SetUpdateResult result = setService.update(setId, payload, terms);

        if (result.hasErrors()) {
            return SetFormResult.error(
                    result.getErrors(),
                    payload,
                    terms);
        }

        return SetFormResult.redirect("/set/" + setId);
    }

}

