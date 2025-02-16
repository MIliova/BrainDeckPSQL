package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRepository extends JpaRepository<Set, Integer> {

    List<Set> findAllByUserId(Integer userId);

    List<Set> findAllByFolderId(Integer folderId);
}
