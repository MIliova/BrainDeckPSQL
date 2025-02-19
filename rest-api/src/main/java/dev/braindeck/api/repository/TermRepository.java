package dev.braindeck.api.repository;

import dev.braindeck.api.entity.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<TermEntity, Integer> {
    List<TermEntity> findAllBySetId(Integer setId);

    void deleteBySetId(Integer setId);
}

