package dev.braindeck.web.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.*;

@RequiredArgsConstructor
public class RestClientLanguagesRestClientImpl implements LanguagesRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<Map<String, Map<Integer, String>>> LANGUAGES_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public  Map<String, Map<Integer, String>>  getAll(){
        return this.restClient
                .get()
                .uri("/api/languages")
                .retrieve()
                .body(LANGUAGES_TYPE_REFERENCE);
    }





//    @Override
//    public String getById(Integer id){
//        try {
//            return this.restClient.get()
//                    .uri("/api/languages/{id}", id)
//                    .retrieve()
//                    .body(String.class);
//        } catch (HttpClientErrorException.NotFound exception) {
//            ProblemDetail problemDetail =  exception.getResponseBodyAs(ProblemDetail.class);
//            if(problemDetail != null) {
//                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
//            }
//            throw new ProblemDetailException("Problem detail is null");
//        }
//    }

}
