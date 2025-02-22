package dev.braindeck.api.controller.payload;

import dev.braindeck.api.entity.FolderEntity;
import dev.braindeck.api.entity.SetEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class UserPayload {

    Integer id;

    @NotNull
    @Size(min = 1, max = 50)
    String name;

    @NotNull
    @Size(min = 1, max = 50)
    String email;

    @NotNull
    @Size(min = 1, max = 70)
    String password;

    List<SetEntity> sets = new ArrayList<>();

    List<FolderEntity> Folder = new ArrayList<>();
}
