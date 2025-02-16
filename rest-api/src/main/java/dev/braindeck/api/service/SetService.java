package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.entity.Term;

import java.util.List;

public interface SetService {
    List<Set> findAllByUserId(int userId);

    List<Set> findAllByFolderId(int folderId);


    Set createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, Integer userId, List<NewTerm> jsonTerms);

    Set findSetById(int setId);

    void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> jsonTerms);

    void deleteSet(int setId);
}

