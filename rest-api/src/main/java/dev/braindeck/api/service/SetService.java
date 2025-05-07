package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithCountDto;
import dev.braindeck.api.entity.*;

import java.util.List;

public interface SetService {
    List<SetWithCountDto> findAllByUserId(int userId);

    SetDto createSet(String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<NewTermPayload> terms);

    SetDto findSetById(int setId);

    void updateSet(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<UpdateTermPayload> terms);

    void deleteSet(int setId);

}


