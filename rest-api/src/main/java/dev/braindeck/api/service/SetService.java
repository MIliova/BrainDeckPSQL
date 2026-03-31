package dev.braindeck.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.*;
import dev.braindeck.api.entity.*;

import java.util.List;

public interface SetService {

    SetCreatedDto create(int userId,
                         String title,
                         String description,
                         int termLanguageId,
                         int descriptionLanguageId,
                         List<NewTermPayload> terms);

    SetDto createFromDraft(
            int userId,
            int draftId,
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId);

    SetShortDto findShortById(int id);

    List<SetWithTCntUInfoDto> findAllByUserId(int userId);

    SetEditDto findSetEditDtoById(int userId, int id);

    SetEntity findById(int userId, int id);

    void autoUpdate (int userId, int id, JsonNode body);

    void update(int id, String title, String description,
                int termLanguageId, int descriptionLanguageId, List<UpdateTermPayload> terms, int userId);

    void delete(int id, int userId);


}


