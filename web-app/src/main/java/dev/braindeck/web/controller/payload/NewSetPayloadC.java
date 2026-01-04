package dev.braindeck.web.controller.payload;

import dev.braindeck.web.entity.NewTermDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewSetPayloadC {
    private Integer id;

    @NotBlank(message = "{error.set.title.not.blank}")
    @Size(min = 1, max = 256, message = "{error.set.title.size}")
    private String title;

    @NotBlank(message = "{error.set.description.not.blank}")
    @Size(min = 1, max = 500, message = "{error.set.description.size}")
    private String description;

    @NotNull(message = "{error.set.termLanguageId.not.null}")
    private Integer termLanguageId;

    @NotNull(message = "{error.set.descriptionLanguageId.not.null}")
    private Integer descriptionLanguageId;

    private String terms; //для Thymeleaf, возможно позже надо убрать, если оно не нужно при ректировании и для черновиков
}
