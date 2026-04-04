package dev.braindeck.web.client;

import dev.braindeck.web.entity.SetShortDto;
import dev.braindeck.web.entity.SetWithCountDto;

import java.util.List;

public interface FoldersRestClient {

    SetShortDto findById(int id);

    List<SetWithCountDto> findAll(int userId);

}
