package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.DraftPayload;
import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.dto.NewDraftDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.UserEntity;

public interface DraftService {

    NewDraftDto create(DraftPayload payload, UserEntity user);

    DraftEntity create(UserEntity user);

    void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId, int currentUserId);

    void delete(int id, int currentUserId);

    DraftEntity findEntityById(int id, int currentUserId);

    DraftEntity findEntityOrCreate(UserEntity user, int draftId);

    DraftDto findFirstByUserId(int userId);


}


