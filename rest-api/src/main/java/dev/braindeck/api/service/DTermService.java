package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateDTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;

import java.util.List;

public interface DTermService {
    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

    TermDto create(DraftSetEntity draft, NewDTermPayload term);

    List<TermDto> create(DraftSetEntity draft, List<NewDTermPayload> terms);

    void update(int draftId, int termId, UpdateDTermPayload payload);

    void delete(int draftId, int id);

    void deleteByDraftId(int draftId);

    List<TermDto> findById(int draftId);

}
