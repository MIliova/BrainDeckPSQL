package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;
import dev.braindeck.web.entity.*;
import java.util.List;
import java.util.Optional;

public interface MySetsRestClient {

    SetDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
                  List<NewTermPayload> terms);

    void update(int id,
                String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<UpdateTermPayload> terms);

    void delete(int id);

    Optional<SetDto> findMySetById(int id);

}
