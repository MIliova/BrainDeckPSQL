package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewDraftPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.entity.DraftSetEntity;
import dev.braindeck.api.entity.UserEntity;

public interface DraftService {

    DraftSetDto create(UserEntity user, NewDraftPayload payload);

    void update(int draftId, String title, String description, int termLanguageId, int descriptionLanguageId);

    void delete(int id);

    DraftSetEntity findEntityById(int setId);

    DraftSetDto findFirstByUserId(int userId);

    DraftSetDto findByIdForUser(int draftId);

    }


