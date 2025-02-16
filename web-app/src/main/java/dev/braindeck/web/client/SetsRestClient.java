package dev.braindeck.web.client;

import dev.braindeck.web.entity.NewTerm;
import dev.braindeck.web.entity.Set;
import dev.braindeck.web.entity.Term;

import java.util.List;
import java.util.Optional;

public interface SetsRestClient {
    List<Set> findAllSets();

    Set createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTerm> terms);

    Optional<Set> findSetById(int setId);

    void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> terms);

    void deleteSet(int setId);
}
