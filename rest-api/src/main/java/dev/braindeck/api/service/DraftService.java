package dev.braindeck.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.UserEntity;

public interface DraftService {

//    NewDraftDto create(DraftPayload payload, UserEntity user);

//    DraftEntity create(UserEntity user);

    void autoUpdate(int userId, int id, JsonNode body);

    void delete(DraftEntity draftEntity);

    DraftDto deleteAndCreate(int draftId, UserEntity user);

    DraftDto getDraftDto(int userId);

    DraftDto getDraftDtoById(int userId, int draftId);

    DraftEntity findOrCreateDraftEntity(int userId);

    DraftEntity findOrCreateDraftEntityById(int userId, int draftId);

    DraftEntity findDraftEntityById(int userId, int draftId);






    }


