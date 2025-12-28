package dev.braindeck.web.controller.exception;

import lombok.Getter;
import org.springframework.web.client.RestClientResponseException;

@Getter
public class BadRequestException extends RuntimeException {

    private final RestClientResponseException restException;

    public BadRequestException(RestClientResponseException restException) {
        super(restException.getMessage(), restException);
        this.restException = restException;
    }
}
