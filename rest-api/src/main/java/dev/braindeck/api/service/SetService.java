package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetDto;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.entity.TermEntity;
import dev.braindeck.api.entity.User;

import java.util.List;

public interface SetService {
    List<SetDto> findAllByUserId(int userId);



    SetDto createSet(String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<NewTermPayload> terms);

    SetDto findSetById(int setId);

    void updateSet(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<UpdateTermPayload> terms);

    void deleteSet(int setId);
}

