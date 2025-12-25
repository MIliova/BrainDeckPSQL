package dev.braindeck.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetFormDto {
    private Integer id;
    private String title;
    private String description;
    private Integer termLanguageId;
    private Integer descriptionLanguageId;
    private List<TermDto> terms;
}
