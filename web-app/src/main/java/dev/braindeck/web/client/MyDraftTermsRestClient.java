package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.TermDto;

import java.util.List;

public interface MyDraftTermsRestClient {

    SetDto create(int setId, List<NewTermPayload> terms);
    void update(List<TermDto> terms);
    void delete(int termId);


}
