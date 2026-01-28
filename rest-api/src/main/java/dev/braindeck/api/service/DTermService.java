package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.NewDTermDto;

import java.util.List;

public interface DTermService {
    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

    NewDTermDto autoCreate(int userId, int draftId, DTermPayload term);

    void autoUpdate(int userId, int draftId, int termId, DTermPayload payload);

    List<NewDTermDto> create(int userId, int draftId, List<DTermPayload> terms);

    void delete(int termId, int draftId, int currentUserId);

}
