package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.SetOfTerms;
import dev.braindeck.api.entity.Term;

import java.util.List;

public interface SetOfTermsService {
    List<SetOfTerms> findAllSets();

    SetOfTerms createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTerm> jsonTerms);

    SetOfTerms findSetById(int setId);

    void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> jsonTerms);

    void deleteSet(int setId);
}

