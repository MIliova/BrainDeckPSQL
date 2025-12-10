package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetService {

    SetDto create(String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<NewTermPayload> terms);

    void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId, List<UpdateTermPayload> terms, int currentUseId);

    void delete(int id, int currentUseId);

    SetDto findByIdForUser(int id, int currentUserId);

    SetDto findById(int id);

    SetEntity findEntityById(int id, int currentUserId);

    List<SetWithTermCountDto> findAllByUserId(int userId);

}


