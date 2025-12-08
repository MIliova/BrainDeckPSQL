package dev.braindeck.api.controller.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewDraftPayload(

        @Size(max = 256, message = "{error.set.title.size}")
        String title,

        @Size(max = 500, message = "{error.set.description.size}")
        String description,

        Integer termLanguageId,

        Integer descriptionLanguageId

) {
}
