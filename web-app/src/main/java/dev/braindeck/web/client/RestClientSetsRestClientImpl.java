package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.RestNewSetPayload;
import dev.braindeck.web.controller.payload.RestUpdateSetPayload;
import dev.braindeck.web.entity.NewTerm;
import dev.braindeck.web.entity.Set;
import dev.braindeck.web.entity.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientSetsRestClientImpl implements SetsRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<Set>> SETS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<Set> findAllSets(int userId) {
        return this.restClient
                .get()
                .uri("/api/user/{userId}/sets", userId)
                .retrieve()
                .body(SETS_TYPE_REFERENCE);
    }

    @Override
    public Set createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTerm> terms) {
        try {
            return this.restClient
                    .post()
                    .uri("/api/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestNewSetPayload( title,  description,  termLanguageId,  descriptionLanguageId, terms))
                    .retrieve()
                    .body(Set.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(Objects.requireNonNull(problemDetail.getProperties()).get("errors"));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public Optional<Set> findSetById(int setId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/api/set/{setId}", setId)
                    .retrieve()
                    .body(Set.class));
        } catch (HttpClientErrorException.NotFound exception) {
            ProblemDetail problemDetail =  exception.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> terms) {
        try {
            System.out.println(terms);

            this.restClient
                    .patch()
                    .uri("/api/set/{setId}/edit", setId)
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
            this.restClient.delete()
                    .uri("/api/set/{setId}/delete", setId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception.getResponseBodyAsString());
        }
    }
}
