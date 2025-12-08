package dev.braindeck.api.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewDTermPayload {

    @Size(min = 0, max = 950)
    private String term;

    @Size(min = 0, max = 950)
    private String description;
}
