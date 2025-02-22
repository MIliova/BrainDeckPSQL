package dev.braindeck.api.controller.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FolderPayload(

        @NotNull
        Integer id,

        @NotNull
        @Size(min = 1, max = 50)
        String name,

        @NotEmpty(message = "{error.set.terms.not.blank}")
        @Valid
        List<UpdateSetPayload> sets
) {
}
