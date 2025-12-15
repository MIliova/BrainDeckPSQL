package dev.braindeck.web.controller.payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewTermPayload {

        @NotBlank
        @Size(min = 1, max = 950)
        private String term;

        @Size(min = 0, max = 950)
        private String description;

}
