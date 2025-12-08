package dev.braindeck.api.controller.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewFolderPayload(

        @NotBlank(message = "{error.folder.name.blank}")
        @Size(max = 50)
        String name,

        @NotEmpty(message = "{error.set.terms.not.blank}")
        @Valid
        List<UpdateSetPayload> sets
) {
}
