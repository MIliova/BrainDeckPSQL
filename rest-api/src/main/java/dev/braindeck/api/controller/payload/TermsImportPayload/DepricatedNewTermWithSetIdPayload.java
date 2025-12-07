package dev.braindeck.api.controller.payload.TermsImportPayload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepricatedNewTermWithSetIdPayload {

    @NotBlank
    @Size(min = 1, max = 950)
    private String term;

    @Size(min = 0, max = 950)
    private String description;

    @NotBlank
    private String setId;
}
