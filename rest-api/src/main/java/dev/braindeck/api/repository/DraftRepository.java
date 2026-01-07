package dev.braindeck.api.repository;

import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.entity.NewDraftEntity;
import dev.braindeck.api.entity.DraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DraftRepository extends JpaRepository<DraftEntity, Integer> {

    Optional<DraftEntity> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);

    DraftEntity save(NewDraftEntity draftEntity);

}