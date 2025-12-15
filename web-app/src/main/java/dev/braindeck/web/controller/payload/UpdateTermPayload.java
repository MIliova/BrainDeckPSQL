package dev.braindeck.web.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTermPayload (

        @NotNull
        @Positive
        Integer id,

        @NotBlank
        @Size(min = 1, max = 950)
        String term,

        @Size(min = 0, max = 950)
        String description,

        @NotNull
        @Positive
        Integer setId
) {
}
