package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;

import java.util.List;
import java.util.Optional;

public interface SetsRestClient {

    SetShortDto findSetById(int setId);

    List<SetWithCountDto> findAllSets(int userId);

}
