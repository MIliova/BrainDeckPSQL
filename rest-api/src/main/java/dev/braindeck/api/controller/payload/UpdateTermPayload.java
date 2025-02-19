package dev.braindeck.api.controller.payload;

import dev.braindeck.api.entity.SetEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
