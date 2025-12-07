package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetService {
    List<SetWithTermCountDto> findAllByUserId(int userId);

    SetDto create(String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<NewTermPayload> terms);

    SetDto findById(int setId);

    void update(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<UpdateTermPayload> terms);

    void delete(int setId);

    SetEntity findEntityById(int setId);
}


