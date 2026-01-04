package dev.braindeck.web.controller.payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTermPayload {
        private Integer id;

        @NotBlank(message = "{error.set.term.not.blank}")
        @Size(min = 1, max = 950, message = "{error.set.term.size}")
        private String term;

        @Size(min = 0, max = 950, message = "{error.set.term.description.size}")
        private String description;

}
