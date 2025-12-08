package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.RestNewSetPayload;
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
public class MySetsRestClientImpl implements MySetsRestClient {

    private final RestClient restClient;

    @Override
    public SetDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms) {
        try {
            return this.restClient
                    .post()
                    .uri("/api/users/me/sets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestNewSetPayload( title,  description,  termLanguageId,  descriptionLanguageId, terms))
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
    public void update(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<TermDto> terms) {
        try {
            System.out.println(terms);
            this.restClient
                    .patch()
                    .uri("/api/users/me/sets/{setId}", setId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestUpdateSetPayload( setId, title,  description,  termLanguageId,  descriptionLanguageId, terms))
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
    public void delete(int setId) {
        try {
            this.restClient
                    .delete()
                    .uri("/api/users/me/sets/{setId}", setId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception.getResponseBodyAsString());
        }
    }

    @Override
    public SetDto createFromDraft(int draftId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms) {
        try {
            return this.restClient
                    .post()
                    .uri("/api/users/me/sets/draft/{draftId}", draftId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestNewSetPayload(title,  description,  termLanguageId,  descriptionLanguageId, terms))
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
}
