package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {
    List<TermDto> findAllBySet(SetEntity set);

    TermDto create(SetEntity entityById, NewTermPayload term);

    List<TermDto> create (SetEntity set, List<NewTermPayload> payloads);

    void update(UpdateTermPayload payload);

    void deleteById(int id);
    void deleteAllBySetId(int setId);

}
