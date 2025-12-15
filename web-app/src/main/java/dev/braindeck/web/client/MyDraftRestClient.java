package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.DTermPayload;
import dev.braindeck.web.entity.DraftDto;
import dev.braindeck.web.entity.NewDraftDto;
import dev.braindeck.web.entity.SetDto;

import java.util.List;
import java.util.Optional;

public interface MyDraftRestClient {

    NewDraftDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId);

    void update(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId);

    void delete(int id);

    Optional<DraftDto> get();

    SetDto createFromDraft(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
                           List<DTermPayload> terms);

}
