package dev.braindeck.api.controller.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestControllerAdvice
public class ControllerAdvice {

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

    // 1. Ошибки валидации JSON: @Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", "errors.400.title", locale);

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
                ));

        pd.setProperty("errors", errors);
        return pd;
    }

    // 2. Ошибки при биндинге Query Params/PathVariables
    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex, Locale locale) {

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", "errors.400.title", locale);

        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
                ));

        pd.setProperty("errors", errors);
        return pd;
    }

    // 3. Невалидный JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonParse(HttpMessageNotReadableException ex, Locale locale) {
        String errors = ex.getLocalizedMessage();
        if (errors != null && errors.length() > 200) errors = errors.substring(0, 200) + "...";

        return problem(HttpStatus.BAD_REQUEST, "errors.400.title", errors, locale);
    }

    // 4. Not found — нет ресурса
    @ExceptionHandler({
            NoSuchElementException.class,
            NoHandlerFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class
    })
    public ProblemDetail handleNotFound(Exception ex, Locale locale) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "errors.404.title", ex.getMessage(), locale);

        pd.setProperty("errors",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        return pd;
    }

    // 5. ConstraintViolationException (валидация на уровне сервисов/параметров)
    @ExceptionHandler({
            ConstraintViolationException.class,
            DraftExistException.class
    })
    public ProblemDetail handleConstraintViolation(Exception ex, Locale locale) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", ex.getMessage(), locale);

        pd.setProperty("errors",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        return pd;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex, Locale locale) {
        ProblemDetail pd = problem(HttpStatus.FORBIDDEN, "errors.403.title", ex.getMessage(), locale);
        pd.setProperty("errors",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
    }

}



