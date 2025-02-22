package dev.braindeck.api.repository;

import dev.braindeck.api.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<FolderEntity, Integer> {
    List<FolderEntity> findAllByUserId(Integer userId);
}
