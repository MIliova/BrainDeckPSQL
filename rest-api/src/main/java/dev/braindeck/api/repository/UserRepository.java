package dev.braindeck.api.repository;

import dev.braindeck.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {

//    @Query(value = "SELECT t_user.*, t_draft_sets.set_id FROM t_user " +
//            "LEFT JOIN t_draft_sets ON t_user.id = t_draft_sets.user_id  WHERE t_user.id = :userId", nativeQuery = true)
//    Optional<UserWithDraftDto> findByIdWithDraft(Integer id);
}


