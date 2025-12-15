package dev.braindeck.web.controller.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final MessageSource messageSource;

    private ProblemDetail problem(HttpStatus status, String titleKey, Locale locale) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(titleKey, null, titleKey, locale)
        );
        pd.setTitle(messageSource.getMessage(titleKey, null, titleKey, locale));
        return pd;
    }


    // 1. Ошибки валидации JSON: @Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);

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

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);

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
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);

        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 200) msg = msg.substring(0, 200) + "...";

        pd.setProperty("errors", msg);
        return pd;
    }

    // 4. Not found — нет ресурса
    @ExceptionHandler({
            NoSuchElementException.class,
            NoHandlerFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class
    })
    public ProblemDetail handleNotFound(Exception ex, Locale locale) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "errors.404.title", locale);

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

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);

        pd.setProperty("errors",
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        return pd;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e,
                                               Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
        return "error/404";
    }

    @ExceptionHandler(JsonProcessingException.class)
    public String handleJson(JsonProcessingException ex, Model model) {
        model.addAttribute("termsError", "error.set.terms.invalid");
        return "new-set";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(
            MethodArgumentNotValidException ex,
            Model model
    ) {
        model.addAttribute("errors", ex.getBindingResult());
        return "new-set";
    }
    @ExceptionHandler(BadRequestException.class)
    public String handleApiError(
            BadRequestException ex,
            Model model
    ) {
        model.addAttribute("apiErrors", ex.getMessage());
        return "new-set";
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleTermsValidation(
            ConstraintViolationException ex,
            Model model
    ) {
        model.addAttribute("termsError", "error.set.terms.invalid");
        return "new-set";
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleTermsError(Model model) {
        model.addAttribute("termsError", "error.set.terms.invalid");
        return "new-set";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleTermsValidation(Model model) {
        model.addAttribute("termsError", "error.set.terms.invalid");
        return "new-set";
    }


}



