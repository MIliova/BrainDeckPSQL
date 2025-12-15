package dev.braindeck.web.controller;

import dev.braindeck.web.controller.exception.ProblemDetailException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionController {

    private final MessageSource messageSource;

    @ExceptionHandler(JsonParseException.class)
    public String handleJsonParseException(Exception e, Model model, HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        return "error/404";
    }

    @ExceptionHandler({
            NoHandlerFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class,
            org.springframework.web.HttpRequestMethodNotSupportedException.class
    })
    public String handleNoHandlerOrNoResourceFoundException(Exception e, Model model, HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        return "error/404";
    }
    @ExceptionHandler(ProblemDetailException.class)
    public String handleProblemDetailException(Exception e, Model model, HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage("errors.problem.detail.exception.null", new Object[0], "errors.problem.detail.exception.null", locale));
        return "error/500";
    }
}


