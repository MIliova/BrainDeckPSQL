package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {

    TermDto create(SetEntity set, NewDTermPayload term);

    List<TermDto> create (SetEntity set, List<NewDTermPayload> payloads);

    void update(int termId, int setId, int currentUserId, UpdateTermPayload payload);

    void delete(int termId, int setId, int currentUserId);

//    void deleteAllBySetId(int setId);

    List<TermDto> findAllBySet(SetEntity set);

}
