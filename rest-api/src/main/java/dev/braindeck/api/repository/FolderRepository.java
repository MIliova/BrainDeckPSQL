package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findAllByUserId(Integer userId);
}
