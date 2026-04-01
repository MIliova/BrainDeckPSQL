package dev.braindeck.web.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFolderPayload(

        @NotBlank(message = "{error.set.title.not.blank}")
        @Size(min = 1, max = 25, message = "{error.set.title.size}")
        String title

) {
}
