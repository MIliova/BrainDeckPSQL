package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository {
    List<Term> findTermsBySetId(Integer setId);

    void save(Term term);

    Optional<Term> findById(Integer id);

    void deleteTermsBySetId(Integer setId);
}

