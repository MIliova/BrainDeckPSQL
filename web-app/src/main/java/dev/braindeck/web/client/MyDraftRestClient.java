package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.DraftDto;
import dev.braindeck.web.entity.SetCreatedDto;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.SetFullDto;

import java.util.List;
import java.util.Optional;

public interface MyDraftRestClient {

    Optional<DraftDto> getOrCreate();

    SetCreatedDto createFromDraft(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
                                  List<NewTermPayload> terms);

}
