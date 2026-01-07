package dev.braindeck.web.service;

import dev.braindeck.web.client.MyDraftRestClient;
import dev.braindeck.web.client.MySetsRestClient;
import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.SetCreatedDto;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.SetFullDto;
import dev.braindeck.web.utills.ProblemDetailParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SetService {

    private final MySetsRestClient mySetsRestClient;
    private final MyDraftRestClient myDraftRestClient;

    public SetCreationResult create(
            NewSetPayloadC payload,
            List<NewTermPayload> terms,
            Integer draftId ){

        try {
            if (draftId != null) {
                SetCreatedDto set =  myDraftRestClient.createFromDraft(
                        draftId,
                        payload.getTitle(),
                        payload.getDescription(),
                        payload.getTermLanguageId(),
                        payload.getDescriptionLanguageId(),
                        terms
                );
                return SetCreationResult.success(set);
            }
            SetCreatedDto set =  mySetsRestClient.create(
                    payload.getTitle(),
                    payload.getDescription(),
                    payload.getTermLanguageId(),
                    payload.getDescriptionLanguageId(),
                    terms
            );
            return SetCreationResult.success(set);
        } catch (BadRequestException e) {
//            List<FieldErrorDto> errors = Util.problemDetailErrorToDtoList(e.getJsonObject());
            Map<String, String> errors = ProblemDetailParser.parse(e.getRestException());
            return SetCreationResult.error(errors);
        }
    }

    public SetUpdateResult update(
            Integer setId,
            UpdateSetPayload payload,
            List<UpdateTermPayload> terms){
        try {
            mySetsRestClient.update(
                    setId,
                    payload.title(),
                    payload.description(),
                    payload.termLanguageId(),
                    payload.descriptionLanguageId(),
                    terms
            );
            return SetUpdateResult.empty();
        } catch (BadRequestException e) {
//            List<FieldErrorDto> errors = Util.problemDetailErrorToDtoList(e.getJsonObject());
            Map<String, String> errors = ProblemDetailParser.parse(e.getRestException());
            return SetUpdateResult.error(errors);
        }
    }


}
