package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetCreatedDto;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetShortDto;
import dev.braindeck.api.dto.SetWithTCntUInfoDto;
import dev.braindeck.api.entity.*;

import java.util.List;

public interface SetService {

    SetDto createFromDraft(
            int draftId,
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId,
            UserEntity user);
    SetCreatedDto create(String title, String description, int termLanguageId, int descriptionLanguageId,
                         int userId, List<NewTermPayload> terms);

    void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId, List<UpdateTermPayload> terms, int currentUseId);

    void delete(int id, int currentUseId);

    SetDto findByIdForUser(int id, int currentUserId);

    SetShortDto findById(int id);

    SetEntity findEntityById(int id, int currentUserId);

    List<SetWithTCntUInfoDto> findAllByUserId(int userId);

}


