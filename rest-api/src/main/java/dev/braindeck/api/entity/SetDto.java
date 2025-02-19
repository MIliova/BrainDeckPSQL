package dev.braindeck.api.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public record SetDto(
        Integer id,
        String title,
        String description,
        Integer termLanguageId,
        Integer descriptionLanguageId,
        UserDto user,
        List<TermDto> terms
) {
}
