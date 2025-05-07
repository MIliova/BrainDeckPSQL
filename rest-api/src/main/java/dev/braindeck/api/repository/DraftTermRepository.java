package dev.braindeck.api.repository;

import dev.braindeck.api.entity.DraftTermEntity;
import dev.braindeck.api.entity.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftTermRepository extends JpaRepository<DraftTermEntity, Integer> {
    List<DraftTermEntity> findAllByDraftId(Integer draftId);

    @Modifying
    void deleteByDraftId(Integer id);

}

