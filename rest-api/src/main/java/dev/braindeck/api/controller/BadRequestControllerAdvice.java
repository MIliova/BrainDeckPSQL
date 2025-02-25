package dev.braindeck.api.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException( BindException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title",
                                new Object[0], "errors.400.title", locale));
        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
//                        .map(ObjectError::getObjectName)
//                        .toList());
                        .filter(error -> error instanceof FieldError)
                        .map(error->(FieldError)error)
                        .collect(Collectors.groupingBy(
                            FieldError::getField,
                            Collectors.mapping(
                                FieldError::getDefaultMessage,
                                Collectors.joining(", "))
                            )
                        )
        );
        System.out.println(problemDetail);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(HttpMessageNotReadableException e, Locale locale) {
        System.out.println(e.getMessage());
        System.out.println(e.getLocalizedMessage());
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title",
                                new Object[0], "errors.400.title", locale));
        String errorMessage = e.getLocalizedMessage();
        if (errorMessage.length() > 50) {
            errorMessage = errorMessage.substring(0, 50).concat("...");
        }
        problemDetail.setProperty("errors", errorMessage);
        return ResponseEntity.badRequest().body(problemDetail);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException e, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,
                        this.messageSource.getMessage("errors.404.title",
                            new Object[0], "errors.404.title", locale));
        problemDetail.setProperty("errors", this.messageSource.getMessage(e.getMessage(),
                new Object[0], e.getMessage(), locale));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }


    @ExceptionHandler({
            NoHandlerFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class,
            org.springframework.web.HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ProblemDetail> handleNoHandlerOrNoResourceFoundException(Exception e, Model model, HttpServletResponse response, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,
                        this.messageSource.getMessage("errors.404.title",
                                new Object[0], "errors.404.title", locale));
        problemDetail.setProperty("errors", this.messageSource.getMessage(e.getMessage(),
                new Object[0], e.getMessage(), locale));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(Exception e, Model model, HttpServletResponse response, Locale locale) {

        System.out.println("Exception from BadRequestControllerAdvice=" + e.getMessage());


        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title",
                                new Object[0], "errors.400.title", locale));
        problemDetail.setProperty("errors", this.messageSource.getMessage(e.getMessage(),
                new Object[0], e.getMessage(), locale));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}

//    @ExceptionHandler(JsonParseException.class)
//    public ResponseEntity<ProblemDetail> handleJsonParseException( JsonParseException exception, Locale locale) {
//        ProblemDetail problemDetail = ProblemDetail
//                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
//                        this.messageSource.getMessage("errors.400.title",
//                                new Object[0], "errors.400.title", locale));
//        problemDetail.setProperty("errors", exception.getMessage());
//        return ResponseEntity.badRequest().body(problemDetail);
//    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ProblemDetail> handleIllegalStateException( IllegalStateException exception, Locale locale) {
//        ProblemDetail problemDetail = ProblemDetail
//                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
//                        this.messageSource.getMessage("errors.400.title",
//                                new Object[0], "errors.400.title", locale));
//        problemDetail.setProperty("errors",
//                exception.getMessage());
//        return ResponseEntity.badRequest().body(problemDetail);
//    }





//Map<String, String> errors = bindingResult.getAllErrors().stream()
//        .filter(error -> error instanceof FieldError)
//        .map(error->(FieldError)error)
//        .collect(Collectors.groupingBy(
//                        FieldError::getField,
//                        Collectors.mapping(
//                                FieldError::getDefaultMessage,
//                                Collectors.joining(", "))
//                )
//        );
//
//        return errors.keySet().stream()
//                .map(error -> new FieldErrorDto(error, errors.get(error)))
//        .toList().reversed();
//
//    }


