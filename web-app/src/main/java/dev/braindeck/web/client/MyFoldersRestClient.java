package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;

import java.util.List;
import java.util.Optional;

public interface MyFoldersRestClient {

    FolderCreatedDto create(String title);

    void update(int id, String title);

    void delete(int id);

    Optional<FolderEditDto> findById(int id);

    List<FolderWithCountDto> findAll();


}
