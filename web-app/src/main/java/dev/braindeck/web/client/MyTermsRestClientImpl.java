package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.RestUpdateSetPayload;
import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public SetDto create(int setId, List<NewTermPayload> terms) {
        try {
            return this.restClient
                    .post()
                    .uri("/api/me/sets/{setId}/terms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(terms)
                    .retrieve()
                    .body(SetDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(Objects.requireNonNull(problemDetail.getProperties()).get("errors"));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void update(List<TermDto> terms) {
        try {
            System.out.println(terms);
            this.restClient
                    .patch()
                    .uri("/api/me/terms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(terms)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(Objects.requireNonNull(problemDetail.getProperties()).get("errors"));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void delete(int termId) {
        try {
            this.restClient
                    .delete()
                    .uri("/api/me/terms/{termId}", termId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception.getResponseBodyAsString());
        }
    }
}
