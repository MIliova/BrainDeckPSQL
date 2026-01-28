package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {

//    NewDTermDto autoCreate(int userId, int draftId, DTermPayload payload);
//
//    void autoUpdate(int userId, int draftId, int termId, DTermPayload payload);

    TermDto create(int userId, int setId, DTermPayload term);

    List<TermDto> create (int userId, int setId, List<DTermPayload> payloads);

    void update(int termId, int setId, int currentUserId, UpdateTermPayload payload);

    void delete(int termId, int setId, int currentUserId);

//    void deleteAllBySetId(int setId);

    List<TermDto> findAllBySet(SetEntity set);

    List<TermDto> findAllBySetId(int setId);

}
