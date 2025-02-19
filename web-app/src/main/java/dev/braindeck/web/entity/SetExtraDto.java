package dev.braindeck.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetExtraDto {
    private String termLangName;
    private String descriptionLangName;
}
