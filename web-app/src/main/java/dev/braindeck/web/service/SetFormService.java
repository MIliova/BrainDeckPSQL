package dev.braindeck.web.service;

import dev.braindeck.web.controller.FieldErrorDto;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.utills.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SetFormService {
    private final ModelPreparationService modelPreparationService;
    private final TermParser termParser;
    private final MessageSource messageSource;
    private final SetCreationService setCreationService;

    public SetFormResult create(
            String payloadTerms,
            NewSetPayload payload,
            BindingResult bindingResult,
            Integer draftId
    ) {

        if (bindingResult.hasErrors()) {
            return SetFormResult.error(Map.of(
                    "bindingErrors", bindingResult
            ));
        }

        TermParser.ParseResult parseResult;

        try {
            parseResult = termParser.parse(payloadTerms);
        } catch (IllegalArgumentException e) {
            return SetFormResult.error(Map.of(
                    "errors", Map.of("general", e.getMessage())
            ));
        }

        if (parseResult.hasErrors()) {
            return SetFormResult.error(Map.of(
                    "termErrors", parseResult.getTermErrors(),
                    "terms", parseResult.getTerms()
            ));
        }

        List<NewTermPayload> terms = parseResult.getTerms();

        SetCreationResult creationResult =
                setCreationService.createSet(payload, parseResult.getTerms(), draftId);

        if (creationResult.hasErrors()) {
            return SetFormResult.error(Map.of(
                    "errors", creationResult.getErrors(),
                    "payload", payload,
                    "terms", parseResult.getTerms()
            ));
        }

        SetDto set = creationResult.getSet();
        return SetFormResult.redirect("/set/" + set.id());

    }

}
