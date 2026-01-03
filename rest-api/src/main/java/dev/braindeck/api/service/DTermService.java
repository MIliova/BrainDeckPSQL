package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.UserEntity;

import java.util.List;

public interface DTermService {
    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

    NewDTermDto create(UserEntity user, int draftId, DTermPayload term);

    List<NewDTermDto> create(UserEntity user, int draftId, List<DTermPayload> terms);

    void update(int termId, int draftId, int currentUserId, DTermPayload payload);

    void delete(int termId, int draftId, int currentUserId);

//    void deleteByDraftId(int draftId, int currentUserId);
//
//    List<TermDto> findDtoByDraftId(int draftId);

}
