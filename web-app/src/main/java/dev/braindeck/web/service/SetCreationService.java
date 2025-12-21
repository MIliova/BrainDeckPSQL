package dev.braindeck.web.service;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.FieldErrorDto;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.NewSetPayload;
import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.utills.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetCreationService {

    private final MySetsRestClient mySetsRestClient;
    private final MyDraftRestClient myDraftRestClient;

    public SetCreationResult createSet(
            NewSetPayload payload,
            List<NewTermPayload> terms,
            Integer draftId ){
        try {
            if (draftId != null) {
                SetDto set =  myDraftRestClient.createFromDraft(
                        draftId,
                        payload.title(),
                        payload.description(),
                        payload.termLanguageId(),
                        payload.descriptionLanguageId(),
                        terms
                );
                return SetCreationResult.success(set);
            }

            SetDto set =  mySetsRestClient.create(
                    payload.title(),
                    payload.description(),
                    payload.termLanguageId(),
                    payload.descriptionLanguageId(),
                    terms
            );
        } catch (BadRequestException e) {
            List<FieldErrorDto> errors =
                    Util.problemDetailErrorToDtoList(e.getJsonObject());
            return SetCreationResult.error(errors);
        }

    }
}

