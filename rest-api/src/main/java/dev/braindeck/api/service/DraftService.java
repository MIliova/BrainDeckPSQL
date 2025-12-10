package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewDraftPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.dto.NewDraftDto;
import dev.braindeck.api.entity.DraftSetEntity;
import dev.braindeck.api.entity.UserEntity;

public interface DraftService {

    NewDraftDto create(NewDraftPayload payload, UserEntity user);

    void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId, int currentUserId);

    void delete(int id, int currentUserId);

    DraftSetEntity findEntityById(int id, int currentUserId);

    DraftSetDto findFirstByUserId(int userId);

//    DraftSetDto findByIdForUser(int draftId);

    }


