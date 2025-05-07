package dev.braindeck.api.controller.payload.TermsImportPayload;

import jakarta.validation.constraints.NotBlank;

@ImportValidation
public record TermsImportPayload(
        @NotBlank (message = "{error.set.import.text}")
        //@Size(min = 1, message = "")
        String text,

        @NotBlank (message = "{error.set.import.colSeparator}")
        String colSeparator,

        @NotBlank (message = "{error.set.import.rowSeparator}")
        String rowSeparator,

        String colCustom,

        String rowCustom
) {
}
