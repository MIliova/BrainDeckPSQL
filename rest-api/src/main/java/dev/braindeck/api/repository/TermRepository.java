package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Integer> {
    List<Term> findAllBySetId(Integer setId);

    List<Term> findAllByUserId(Integer userId);

    void deleteBySetId(Integer setId);
}

