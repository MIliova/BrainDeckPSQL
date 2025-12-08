package dev.braindeck.api.repository;

import dev.braindeck.api.entity.DraftSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DraftRepository extends JpaRepository<DraftSetEntity, Integer> {
    Optional <DraftSetEntity> findFirstByUserId(Integer userId);

//    @Modifying
//    @Query("DELETE FROM DraftSetEntity s WHERE s.id = :id")
//    void deleteById(@Param("id") Integer id);

}