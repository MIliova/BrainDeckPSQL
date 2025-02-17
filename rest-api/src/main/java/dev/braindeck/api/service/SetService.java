package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.entity.Term;
import dev.braindeck.api.entity.User;

import java.util.List;

public interface SetService {
    List<Set> findAllByUserId(int userId);



    Set createSet(String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<NewTerm> terms);

    Set findSetById(int setId);

    void updateSet(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<Term> terms);

    void deleteSet(int setId);
}

