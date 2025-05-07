package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewSetFromDraftPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.entity.UserEntity;

public interface DraftSetService {

    DraftSetDto create(UserEntity user, NewSetFromDraftPayload payload);

    DraftSetDto findFirstByUserId(int userId);

    DraftSetDto findById(int draftId);

    void delete(int id);

    }


