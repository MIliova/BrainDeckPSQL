package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Integer> {
    List<Term> findAllBySetId(Integer setId);

    void deleteBySetId(Integer setId);
}

