package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.NewTermWithSetIdPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;

import java.util.List;

public interface TermService {
    List<TermDto> findAllBySet(SetEntity set);

    List<TermDto> create(SetEntity set, List<NewTermPayload> payloads);

    TermDto create(SetEntity entityById, NewTermWithSetIdPayload term);

    void update(UpdateTermPayload payload);

    void deleteById(int id);
    void deleteAllBySetId(int setId);


}
