package dev.braindeck.api.controller.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewSetPayload(

        @NotBlank(message = "{error.set.title.not.blank}")
        @Size(min = 1, max = 256, message = "{error.set.title.size}")
        String title,

        @NotBlank(message = "{error.set.description.not.blank}")
        @Size(min = 1, max = 500, message = "{error.set.description.size}")
        String description,

        @NotNull(message = "{error.set.termLanguageId.not.null}")
        Integer termLanguageId,

        @NotNull(message = "{error.set.descriptionLanguageId.not.null}")
        Integer descriptionLanguageId,

        @NotEmpty(message = "{error.set.terms.not.blank}")
        @Valid
        List<NewTermPayload> terms
) {
}
