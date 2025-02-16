package dev.braindeck.web.utills;

import dev.braindeck.web.controller.FieldErrorDto;
import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class Util {
    public static List<FieldErrorDto> bindingResultToDtoList(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .map(error->(FieldError)error)
                .collect(Collectors.groupingBy(
                                FieldError::getField,
                                Collectors.mapping(
                                        FieldError::getDefaultMessage,
                                        Collectors.joining(", "))
                        )
                );

        return errors.keySet().stream()
                .map(error -> new FieldErrorDto(error, errors.get(error)))
                .toList().reversed();

    }

    public static List<FieldErrorDto> problemDetailErrorToDtoList(Object errorsObject) {
        if (errorsObject instanceof LinkedHashMap) {
            LinkedHashMap<String, String> errorsMap = (LinkedHashMap<String, String>) errorsObject;
            return errorsMap.entrySet().stream().map(entry->new FieldErrorDto(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
