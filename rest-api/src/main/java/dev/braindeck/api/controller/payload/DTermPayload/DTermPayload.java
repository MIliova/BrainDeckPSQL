
package dev.braindeck.api.controller.payload.DTermPayload;
import jakarta.validation.constraints.Size;

@OneNotBlank
public record DTermPayload(

        @Size(max = 950)
        String term,

        @Size(max = 950)
        String description

) {
}

