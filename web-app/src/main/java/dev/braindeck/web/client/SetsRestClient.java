package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.entity.*;

import java.util.List;
import java.util.Optional;

public interface SetsRestClient {

    UserDto findCurrentUser();

    List<SetWithCountDto> findAllSets(int userId);

    SetDto createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms);

    Optional<SetDto> findSetById(int setId);

    void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> terms);

    void deleteSet(int setId);

    List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom);

    }
