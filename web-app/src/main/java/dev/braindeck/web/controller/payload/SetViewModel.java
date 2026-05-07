package dev.braindeck.web.controller.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetViewModel<T> {
    private Integer id;
    private String title;
    private String description;
    private Integer termLanguageId;
    private Integer descriptionLanguageId;

    private boolean isDraft;

    private List<T> terms;
    private boolean hasTermsErrors;
    private Map<String, String> error;
}
