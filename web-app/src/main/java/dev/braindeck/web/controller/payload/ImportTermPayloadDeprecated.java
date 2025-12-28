package dev.braindeck.web.controller.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportTermPayloadDeprecated {
    String text;
    String colSeparator;
    String rowSeparator;
    String colCustom;
    String rowCustom;
}
