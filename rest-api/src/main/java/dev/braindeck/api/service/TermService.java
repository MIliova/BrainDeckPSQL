package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {
    List<TermDto> findTermsBySetId(int setId);

    void createTerms(SetEntity set, List<NewTermPayload> jsonTerms);

    void updateTerms(List<UpdateTermPayload> jsonTerms);
    void updateTerm(Integer id, String term, String description);

    void deleteTermById(int id);
    void deleteTermsBySetId(int setId);

    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);


}
