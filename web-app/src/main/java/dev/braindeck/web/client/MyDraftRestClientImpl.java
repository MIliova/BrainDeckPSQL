package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.DraftSetDto;
import dev.braindeck.web.entity.NewDraftDto;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.TermDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public NewDraftDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId) {
        try {
            return restClient
                    .post()
                    .uri("/api/me/draft")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestNewDraftPayload(title,  description,  termLanguageId,  descriptionLanguageId))
                    .retrieve()
                    .body(NewDraftDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(Objects.requireNonNull(problemDetail.getProperties()).get("errors"));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void update(int draftId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId) {
        try {
            this.restClient
                    .patch()
                    .uri("/api/me/draft/{draftId}", draftId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestUpdateDraftPayload(draftId, title,  description,  termLanguageId,  descriptionLanguageId))
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
    public void delete(int draftId) {
        try {
            this.restClient
                    .delete()
                    .uri("/api/me/draft/{draftId}", draftId)
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
                    .uri("/api/me/draft/{draftId}/convert", draftId)
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

    @Override
    public Optional<DraftSetDto> get() {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/api/me/draft")
                    .retrieve()
                    .body(DraftSetDto.class));
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail =  exception.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

}
