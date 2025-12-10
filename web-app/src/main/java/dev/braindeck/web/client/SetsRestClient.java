package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;

import java.util.List;
import java.util.Optional;

public interface SetsRestClient {

    UserDto findCurrentUser();

    List<SetWithCountDto> findAllSets(int userId);

    Optional<SetDto> findSetById(int setId);


    Optional<DraftSetDto> findDraftByUserId(int userId);

    Optional<DraftSetDto> findDraftById(int draftId);


    }
