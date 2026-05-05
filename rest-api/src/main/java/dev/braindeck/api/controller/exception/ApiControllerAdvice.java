package dev.braindeck.api.controller.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RestControllerAdvice
public class ApiControllerAdvice {

    private final MessageSource messageSource;

    private ProblemDetail problem(
            HttpStatus status,
            String titleKey,
            String detailKey,
            Locale locale
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(status);

        pd.setTitle(messageSource.getMessage(titleKey, null, titleKey, locale));
        pd.setDetail(messageSource.getMessage(detailKey, null, detailKey, locale));

        return pd;
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleHandlerMethodValidation(HandlerMethodValidationException ex, Locale locale) {

        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                "errors.400.title",
                "Validation failed",
                locale
        );

        Map<String, String> errors = new HashMap<>();

        ex.getAllErrors().forEach(error -> {
            String key = error.getCodes() != null && error.getCodes().length > 0
                    ? error.getCodes()[0]
                    : "global";
            if (key.contains(".")) {
                key = key.substring(key.lastIndexOf(".") + 1);
            }

            errors.merge(key, error.getDefaultMessage(), (a, b) -> a + ", " + b);
        });

        pd.setProperty("errors", errors);
        return pd;
    }
    // 1. Ошибки валидации JSON: @Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {

        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                "errors.400.title",
                ex.getMessage(),
                locale);

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
                ));

//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String key;
//            if (error instanceof FieldError fieldError) {
//                key = fieldError.getField();
//            } else {
//                key = "global";
//            }
//            String message = error.getDefaultMessage();
//            errors.merge(key, message, (a, b) -> a + ", " + b);
//        });

        pd.setProperty("errors", errors);
        return pd;
    }

    // 5. ConstraintViolationException (валидация на уровне сервисов/параметров)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex, Locale locale) {

        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                "errors.400.title",
                ex.getMessage(),
                locale
        );

        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        v -> v.getPropertyPath().toString(),
                        Collectors.mapping(
                                v -> messageSource.getMessage(
                                        v.getMessage(), null, v.getMessage(), locale),
                                Collectors.joining(", ")
                        )
                ));

        pd.setProperty("errors", errors);
        return pd;
    }

    // 4. Not found — нет ресурса
    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFound(Exception ex, Locale locale) {
        ProblemDetail pd = problem(
                HttpStatus.NOT_FOUND,
                "errors.404.title",
                ex.getMessage(),
                locale);

        pd.setProperty("errors", Map.of("general",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        ));

        return pd;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex, Locale locale) {
        ProblemDetail pd = problem(
                HttpStatus.FORBIDDEN,
                "errors.403.title",
                ex.getMessage(),
                locale);

        pd.setProperty("errors",Map.of("general",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        ));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception ex, Locale locale) {

        System.out.println("CLASS = " + ex.getClass().getName());
        ex.printStackTrace();

        ProblemDetail pd = problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "errors.500.title",
                ex.getMessage(),
                locale
        );

        pd.setProperty("errors",Map.of("general",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        ));
        return pd;
    }

    // 2. Ошибки при биндинге Query Params/PathVariables
    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex, Locale locale) {

        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                "errors.400.title",
                ex.getMessage(),
                locale
        );

        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a
                ));

        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonParse(HttpMessageNotReadableException ex, Locale locale) {
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                "errors.400.title",
                "errors.400.invalid_json",
                locale
        );

        pd.setProperty("errors", Map.of("general",
                messageSource.getMessage("errors.json.invalid", null, locale)
        ));
        return pd;
    }

}



