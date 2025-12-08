package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.ImportTermPayload;
import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class MyTermsImportRestClientImpl implements MyTermsImportRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<ImportTermDto>> TERMS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<ImportTermDto> prepare(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom){

        System.out.println(new ImportTermPayload(text,  colSeparator,  rowSeparator,  colCustom, rowCustom));
        System.out.println("colSeparator='"+colSeparator+"'");
        System.out.println("rowSeparator='"+rowSeparator+"'");
        System.out.println("colCustom='"+colCustom+"'");
        System.out.println("rowCustom='"+rowCustom+"'");

        try {
            return this.restClient
                    .post()
                    .uri("/api/import/terms/")
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

}
