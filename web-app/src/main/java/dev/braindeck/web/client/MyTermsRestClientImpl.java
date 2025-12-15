package dev.braindeck.web.client;

import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.exception.ProblemDetailException;
import dev.braindeck.web.controller.payload.DTermPayload;
import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class MyTermsRestClientImpl implements MyTermsRestClient {

    private final RestClient restClient;

    @Override
    public TermDto create(int setId, DTermPayload term) {
        try {
            return restClient
                    .post()
                    .uri("/api/me/set/{setId}/terms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(term)
                    .retrieve()
                    .body(TermDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }
    @Override
    public List<TermDto> create(int setId, List<DTermPayload> terms) {
        try {
            return Objects.requireNonNull(restClient
                    .post()
                    .uri("/api/me/set/{setId}/terms/batch", setId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(terms)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TermDto>>() {
                    }));
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void update(int setId, int termId, DTermPayload term) {
        try {
            restClient
                    .patch()
                    .uri("/api/me/set/{setId}/terms/{termId}", setId, termId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(term)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void delete(int setId, int termId) {
        try {
            restClient
                    .delete()
                    .uri("/api/me/set/{setId}/terms/{termId}", setId, termId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception.getResponseBodyAsString());
        }
    }
}
