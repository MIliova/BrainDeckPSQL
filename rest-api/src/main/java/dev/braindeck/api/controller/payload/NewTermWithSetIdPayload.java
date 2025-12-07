package dev.braindeck.api.controller.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTermWithSetIdPayload {
    @NotNull
    @Positive
    private Integer setId;

    @Size(min = 0, max = 950)
    private String term;

    @Size(min = 0, max = 950)
    private String description;
}
