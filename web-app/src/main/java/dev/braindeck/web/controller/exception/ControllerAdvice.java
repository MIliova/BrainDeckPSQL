package dev.braindeck.web.controller.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.braindeck.web.controller.FieldErrorDto;
import dev.braindeck.web.utills.Util;
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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final MessageSource messageSource;

    // 1. ConstraintViolationException (валидация параметров)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(cv -> messageSource.getMessage(cv.getMessage(), null, cv.getMessage(), locale))
                .toList();

        return Map.of(
                "status", "error",
                "errors", errors
        );
    }

    // 2. BadRequestException из RestClient
    @ExceptionHandler(BadRequestException.class)
    public Map<String, Object> handleBadRequest(BadRequestException ex) {
        List<FieldErrorDto> errors = Util.problemDetailErrorToDtoList(ex.getJsonObject());
        return Map.of(
                "status", "error",
                "errors", errors
        );
    }

    // 3. Любые другие исключения (опционально)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGeneric(Exception ex) {
        return Map.of(
                "status", "error",
                "errors", List.of(ex.getMessage())
        );
    }
}

//@org.springframework.web.bind.annotation.ControllerAdvice
//@RequiredArgsConstructor
//public class ControllerAdvice {
//
//    private final MessageSource messageSource;

//    private ProblemDetail problem(HttpStatus status, String titleKey, Locale locale) {
//        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
//                status,
//                messageSource.getMessage(titleKey, null, titleKey, locale)
//        );
//        pd.setTitle(messageSource.getMessage(titleKey, null, titleKey, locale));
//        return pd;
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        pd.setTitle(messageSource.getMessage("errors.400.title", null, locale));
//
//        List<String> errors = ex.getConstraintViolations().stream()
//                .map(cv -> messageSource.getMessage(cv.getMessage(), null, cv.getMessage(), locale))
//                .toList();
//
//        pd.setProperty("errors", errors);
//        return pd;
//    }
//
//
//
//
//
//
//
//
//    // 1. Ошибки валидации JSON: @Valid @RequestBody
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
//
//        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);
//
//        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
//                .collect(Collectors.groupingBy(
//                        FieldError::getField,
//                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
//                ));
//
//        pd.setProperty("errors", errors);
//        return pd;
//    }
//
//    // 2. Ошибки при биндинге Query Params/PathVariables
//    @ExceptionHandler(BindException.class)
//    public ProblemDetail handleBindException(BindException ex, Locale locale) {
//
//        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);
//
//        Map<String, String> errors = ex.getFieldErrors().stream()
//                .collect(Collectors.groupingBy(
//                        FieldError::getField,
//                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
//                ));
//
//        pd.setProperty("errors", errors);
//        return pd;
//    }
//
//    // 3. Невалидный JSON
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ProblemDetail handleJsonParse(HttpMessageNotReadableException ex, Locale locale) {
//        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "errors.400.title", locale);
//
//        String msg = ex.getLocalizedMessage();
//        if (msg != null && msg.length() > 200) msg = msg.substring(0, 200) + "...";
//
//        pd.setProperty("errors", msg);
//        return pd;
//    }
//
//    // 4. Not found — нет ресурса
//    @ExceptionHandler({
//            NoSuchElementException.class,
//            NoHandlerFoundException.class,
//            org.springframework.web.servlet.resource.NoResourceFoundException.class
//    })
//    public ProblemDetail handleNotFound(Exception ex, Locale locale) {
//        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "errors.404.title", locale);
//
//        pd.setProperty("errors",
//                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
//        );
//
//        return pd;
//    }
//
//
//
//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex) {
//        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
//    }
//
//    @ExceptionHandler(NoSuchElementException.class)
//    public String handleNoSuchElementException(NoSuchElementException e,
//                                               Model model,
//                                               HttpServletResponse response, Locale locale) {
//        response.setStatus(HttpStatus.NOT_FOUND.value());
//        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
//        model.addAttribute("pageTitle", messageSource.getMessage("messages.set.create_new", null, locale));
//        return "error/404";
//    }
//
//    @ExceptionHandler(JsonProcessingException.class)
//    public String handleJson(JsonProcessingException ex, Model model) {
//        model.addAttribute("termsError", "error.set.terms.invalid");
//        return "new-set";
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public String handleValidation(
//            MethodArgumentNotValidException ex,
//            Model model
//    ) {
//        model.addAttribute("errors", ex.getBindingResult());
//        return "new-set";
//    }
//    @ExceptionHandler(BadRequestException.class)
//    public String handleApiError(
//            BadRequestException ex,
//            Model model
//    ) {
//        model.addAttribute("apiErrors", ex.getMessage());
//        return "new-set";
//    }
//    @ExceptionHandler(ConstraintViolationException.class)
//    public String handleTermsValidation(
//            ConstraintViolationException ex,
//            Model model
//    ) {
//        model.addAttribute("termsError", "error.set.terms.invalid");
//        return "new-set";
//    }
//    @ExceptionHandler(IllegalArgumentException.class)
//    public String handleTermsError(Model model) {
//        model.addAttribute("termsError", "error.set.terms.invalid");
//        return "new-set";
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public String handleTermsValidation(Model model) {
//        model.addAttribute("termsError", "error.set.terms.invalid");
//        return "new-set";
//    }
//
//
//}



