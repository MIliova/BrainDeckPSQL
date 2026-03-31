package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {

//    NewDTermDto autoCreate(int userId, int setId, DTermPayload term);
//
//    void autoUpdate(int userId, int draftId, int termId, DTermPayload payload);

    NewDTermDto autoCreate(int userId, int setId, DTermPayload term);

    List<TermDto> autoCreate(int userId, int setId, List<DTermPayload> payloads);

    void autoUpdate(int termId, int setId, int currentUserId, DTermPayload payload);

    void delete(int termId, int setId, int currentUserId);

//    void deleteAllBySetId(int setId);

    List<TermDto> findAllBySet(SetEntity set);

    List<TermDto> findAllBySetId(int setId);

}
