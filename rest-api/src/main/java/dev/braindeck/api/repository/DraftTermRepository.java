package dev.braindeck.api.repository;

import dev.braindeck.api.entity.DTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftTermRepository extends JpaRepository<DTermEntity, Integer> {
    List<DTermEntity> findAllByDraftId(Integer draftId);

    @Modifying
    @Query("DELETE FROM DTermEntity t WHERE t.draft.id = :draftId")
    void deleteAllByDraftId(@Param("draftId") Integer draftId);

}

