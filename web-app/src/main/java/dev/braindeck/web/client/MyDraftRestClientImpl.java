package dev.braindeck.web.client;

import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.exception.ProblemDetailException;
import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.DraftDto;
import dev.braindeck.web.entity.NewDraftDto;
import dev.braindeck.web.entity.SetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MyDraftRestClientImpl implements MyDraftRestClient {

    private final RestClient restClient;

    @Override
    public Optional<DraftDto> get() {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/me/draft")
                    .retrieve()
                    .body(DraftDto.class));
        } catch (HttpClientErrorException.NotFound e) {
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public NewDraftDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId) {
        try {
            return restClient
                    .post()
                    .uri("/api/me/draft")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DraftPayload(title,  description,  termLanguageId,  descriptionLanguageId))
                    .retrieve()
                    .body(NewDraftDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void update(int draftId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId) {
        try {
            restClient
                    .patch()
                    .uri("/api/me/draft/{draftId}", draftId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DraftPayload(title,  description,  termLanguageId,  descriptionLanguageId))
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
    public void delete(int draftId) {
        try {
            restClient
                    .delete()
                    .uri("/api/me/draft/{draftId}", draftId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception.getResponseBodyAsString());
        }
    }

    @Override
    public SetDto createFromDraft(
            int draftId,
            String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
            List<DTermPayload> terms) {
        try {
            return restClient
                    .post()
                    .uri("/api/me/draft/{draftId}/convert", draftId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestDraftPayload(title,  description,  termLanguageId,  descriptionLanguageId, terms))
                    .retrieve()
                    .body(SetDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }


}
