package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.DraftSetDto;
import dev.braindeck.web.entity.NewDraftDto;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.TermDto;

import java.util.List;
import java.util.Optional;

public interface MyDraftRestClient {

    NewDraftDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId);

    void update(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId);

    void delete(int id);

    Optional<DraftSetDto> get();

    SetDto createFromDraft(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms);

}
