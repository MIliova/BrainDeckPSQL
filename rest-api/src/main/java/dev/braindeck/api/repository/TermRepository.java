package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.entity.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<TermEntity, Integer> {
    @Modifying
    @Query("DELETE FROM TermEntity t WHERE t.set.id = :setId")
    void deleteBySetId(@Param("setId") Integer setId);

    List<TermEntity> findAllBySet(SetEntity setEntity);
}

