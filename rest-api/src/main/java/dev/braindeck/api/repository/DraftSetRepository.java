package dev.braindeck.api.repository;

import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.DraftSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DraftSetRepository extends JpaRepository<DraftSetEntity, Integer> {
    Optional <DraftSetEntity> findFirstByUserId(Integer userId);

    @Modifying
    @Query("DELETE FROM DraftSetEntity s WHERE s.id = :id")
    void deleteById(@Param("id") Integer id);

}