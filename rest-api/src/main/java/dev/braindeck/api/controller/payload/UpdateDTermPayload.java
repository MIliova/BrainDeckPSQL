package dev.braindeck.api.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateDTermPayload(

//        @NotNull
//        @Positive
//        Integer id,

        @Size(max = 950)
        String term,

        @Size(max = 950)
        String description,

        @NotNull
        @Positive
        Integer draftId
) {
}
