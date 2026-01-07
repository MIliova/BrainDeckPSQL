package dev.braindeck.web.controller.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftPayload {
    private Integer id;
    private String title;
    private String description;
    private Integer termLanguageId;
    private Integer descriptionLanguageId;
}
