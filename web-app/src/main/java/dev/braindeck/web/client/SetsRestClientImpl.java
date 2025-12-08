package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SetsRestClientImpl implements SetsRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<SetWithCountDto>> SETS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };


    @Override
    public UserDto findCurrentUser(){
        return this.restClient
                .get()
                .uri("/api/current-user")
                .retrieve()
                .body(UserDto.class);
    }

    @Override
    public List<SetWithCountDto> findAllSets(int userId) {
        return this.restClient
                .get()
                .uri("/api/users/{userId}/sets", userId)
                .retrieve()
                .body(SETS_TYPE_REFERENCE);
    }

    @Override
    public Optional<SetDto> findSetById(int setId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/api/sets/{setId}", setId)
                    .retrieve()
                    .body(SetDto.class));
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail =  exception.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }



    @Override
    public Optional<DraftSetDto> findDraftByUserId(int userId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/api/draft/user/{userId}", userId)
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

    @Override
    public Optional<DraftSetDto> findDraftById(int draftId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/api/draft/{draftId}", draftId)
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
