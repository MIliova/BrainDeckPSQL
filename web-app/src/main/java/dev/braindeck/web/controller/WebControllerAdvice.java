package dev.braindeck.web.controller;

import dev.braindeck.web.utills.ProblemDetailParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Locale;
import java.util.Map;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class WebControllerAdvice {

    private String determineView(RestClientResponseException ex, Model model) {
        String currentView = (String) model.getAttribute("currentView");
        if (currentView == null) {
            log.warn("currentView is not set, defaulting to 'error/500'");
            return "error/500";
        }
        return currentView;
    }

    @ExceptionHandler(RestClientResponseException.class)
    public String handleRestClientResponseException(RestClientResponseException ex, Model model) {
        model.addAttribute("errors", ProblemDetailParser.parse(ex));
        return determineView(ex, model);
    }

//    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
//    public String handleBadRequest(HttpClientErrorException.BadRequest ex, Model model) {
//        model.addAttribute("errors", parseProblemDetail(ex));
//        return determineView(ex, model);
//    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public String handleHttpClientError(HttpClientErrorException.NotFound ex, Model model, Locale locale) {
        model.addAttribute("errors", ProblemDetailParser.parse(ex));
        return "error/404";
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public String handleForbidden(HttpClientErrorException.Forbidden ex, Model model, Locale locale) {
        model.addAttribute("errors", ProblemDetailParser.parse(ex));
        return "error/403";
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public String handleInternalServerError(HttpServerErrorException.InternalServerError ex, Model model) {
        model.addAttribute("errors", ProblemDetailParser.parse(ex));
        return "error/500";
    }

//    @ExceptionHandler(Throwable.class)
//    public String handleThrowable(Throwable ex, final Model model) {
//        model.addAttribute("errors", Map.of("general", ex.getMessage()));
//        return "error/500";
//    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("errors", Map.of("general", ex.getMessage()));
        return "error/500";
    }
}




