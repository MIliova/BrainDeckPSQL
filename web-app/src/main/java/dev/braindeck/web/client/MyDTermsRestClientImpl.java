//package dev.braindeck.web.client;
//
//import dev.braindeck.web.controller.exception.BadRequestException;
//import dev.braindeck.web.controller.exception.ProblemDetailException;
//import dev.braindeck.web.controller.payload.DTermPayload;
//import dev.braindeck.web.entity.NewDTermDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.MediaType;
//import org.springframework.http.ProblemDetail;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestClient;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Objects;
//
//@Slf4j
//@RequiredArgsConstructor
//public class MyDTermsRestClientImpl implements MyDTermsRestClient {
//
//    private final RestClient restClient;
//
////    @Override
////    public NewDTermDto create(int draftId, DTermPayload term) {
////        try {
////            return this.restClient
////                    .post()
////                    .uri("/api/me/draft/{draftId}/terms")
////                    .contentType(MediaType.APPLICATION_JSON)
////                    .body(term)
////                    .retrieve()
////                    .body(NewDTermDto.class);
////        } catch (HttpClientErrorException.BadRequest e) {
////            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
////            if(problemDetail != null) {
////                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
////            }
////            throw new ProblemDetailException("Problem detail is null");
////        }
////    }
////    @Override
////    public List<NewDTermDto> create(int draftId, List<DTermPayload> terms) {
////        try {
////            return Objects.requireNonNull(this.restClient
////                            .post()
////                            .uri("/api/me/draft/{draftId}/terms/batch", draftId)
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .body(terms)
////                            .retrieve()
////                            .body(new ParameterizedTypeReference<List<NewDTermDto>>() {
////                            }));
////        } catch (HttpClientErrorException.BadRequest e) {
////            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
////            if(problemDetail != null) {
////                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
////            }
////            throw new ProblemDetailException("Problem detail is null");
////        }
////    }
//
////    @Override
////    public void update(int draftId, int termId, DTermPayload term) {
////        try {
////            restClient
////                    .patch()
////                    .uri("/api/me/draft/{draftId}/terms/{termId}", draftId, termId)
////                    .contentType(MediaType.APPLICATION_JSON)
////                    .body(term)
////                    .retrieve()
////                    .toBodilessEntity();
////        } catch (HttpClientErrorException.BadRequest e) {
////            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
////            if(problemDetail != null) {
////                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
////            }
////            throw new ProblemDetailException("Problem detail is null");
////        }
////    }
////
////    @Override
////    public void delete(int draftId, int termId) {
////        try {
////            restClient
////                    .delete()
////                    .uri("/api/me/draft/{draftId}/terms/{termId}", draftId, termId)
////                    .retrieve()
////                    .toBodilessEntity();
////        } catch (HttpClientErrorException.NotFound exception) {
////            throw new NoSuchElementException(exception.getResponseBodyAsString());
////        }
////    }
//}
