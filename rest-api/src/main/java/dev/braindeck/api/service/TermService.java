package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Term;

import java.util.List;

public interface TermService {
    List<Term> findTermsBySetId(int setId);

    void createTerms(int setId, List<NewTerm> jsonTerms);

    void updateTerms(List<Term> jsonTerms);
    void updateTerm(Integer id, String term, String description);

    void deleteTermsBySetId(int setId);
}
