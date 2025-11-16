package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.*;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RestClientSetsRestClientImpl implements SetsRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<SetWithCountDto>> SETS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<List<ImportTermDto>> TERMS_TYPE_REFERENCE =
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
    public SetDto createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms) {
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
    public void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<TermDto> terms) {
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
    public void deleteSet(int setId) {
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
    public List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom){

        System.out.println(new ImportTermPayload(text,  colSeparator,  rowSeparator,  colCustom, rowCustom));
        System.out.println("colSeparator='"+colSeparator+"'");
        System.out.println("rowSeparator='"+rowSeparator+"'");
        System.out.println("colCustom='"+colCustom+"'");
        System.out.println("rowCustom='"+rowCustom+"'");


        try {
            return this.restClient
                    .post()
                    .uri("/api/terms/prepare-import")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ImportTermPayload(text,  colSeparator,  rowSeparator,  colCustom, rowCustom))
                    .retrieve()
                    .body(TERMS_TYPE_REFERENCE);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(Objects.requireNonNull(problemDetail.getProperties()).get("errors"));
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


    @Override
    public SetDto createSetFromDraft(int draftId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTermPayload> terms) {
        try {
            return this.restClient
                    .post()
                    .uri("/api/draft/{draftId}/create-set", draftId)
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
