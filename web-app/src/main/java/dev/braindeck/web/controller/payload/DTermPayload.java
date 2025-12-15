package dev.braindeck.web.controller.payload;

import jakarta.validation.constraints.Size;

public record DTermPayload(

        @Size(max = 950)
        String term,

        @Size(max = 950)
        String description

) {
}
