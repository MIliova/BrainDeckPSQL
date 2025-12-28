package dev.braindeck.web.utills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class ProblemDetailParser {

    private ProblemDetailParser() {}

    public static Map<String, String> parse(RestClientResponseException e) {
        try {
            var problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            if (problemDetail != null && problemDetail.getProperties() != null) {
                Object errors = problemDetail.getProperties().get("errors");
                if (errors instanceof Map<?, ?> map) {
                    return map.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> String.valueOf(entry.getKey()),
                                    entry -> String.valueOf(entry.getValue())
                            ));
                } else if (errors instanceof Iterable<?> list) {
                    AtomicInteger idx = new AtomicInteger();
                    return StreamSupport.stream(list.spliterator(), false)
                            .collect(Collectors.toMap(
                                    i -> "error_" + (idx.getAndIncrement()),
                                    i -> String.valueOf(i)
                            ));
                }
            }
        } catch (Exception ex) {
            log.error("Cannot parse ProblemDetail from REST response", ex);
        }
        return Map.of("general", "Ошибка при обработке ответа сервера");
    }

}
