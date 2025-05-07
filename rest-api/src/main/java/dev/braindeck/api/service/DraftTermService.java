package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;

import java.util.List;

public interface DraftTermService {
    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

    void createTerms(DraftSetEntity draft, List<NewTermPayload> terms);

    List<TermDto> findTermsByDraftId(int draftId);

    void deleteByDraftId(int id);

}
