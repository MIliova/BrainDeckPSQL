package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.DTermPayload;
import dev.braindeck.web.entity.TermDto;

import java.util.List;

public interface MyTermsRestClient {

    TermDto create(int setId, DTermPayload term);

    List<TermDto>  create(int setId, List<DTermPayload> terms);

    void update(int setId, int termId, DTermPayload term);

    void delete(int setId, int termId);

}
