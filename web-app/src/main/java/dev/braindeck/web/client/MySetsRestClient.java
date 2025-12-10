package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;
import java.util.List;
import java.util.Optional;

public interface MySetsRestClient {

    SetDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms);

    void update(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<TermDto> terms);

    void delete(int setId);

    SetDto createFromDraft(int draftId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms);

    Optional<SetDto> findMySetById(int setId);

}
