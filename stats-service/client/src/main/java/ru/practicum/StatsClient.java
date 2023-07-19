package ru.practicum;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.dto.EndpointHit;
import ru.practicum.dto.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Array.get;

@Service
@Slf4j
@PropertySource(value = {"classpath:statsClient.properties"})
public class StatsClient {
    private static final String STATS_PATH = "/stats";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;
    @Value("${stats.server.url}")
    private String baseUrl;
    private final WebClient client;

    public StatsClient() {
        this.client = WebClient.create(baseUrl);
    }

//    public ResponseEntity<List<ViewStats>> getStats(String start, String end, List<String> uris, Boolean unique) {
//        return this.client.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/stats")
//                        .queryParam("start", start)
//                        .queryParam("end", end)
//                        .queryParam("uris", uris)
//                        .queryParam("unique", unique)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .toEntityList(ViewStats.class)
//                .doOnNext(c -> log.info("Get stats with param: start date {}, end date {}, uris {}, unique {}",
//                        start, end, uris, unique))
//                .block();
//    }
public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, boolean unique, List<String> urls) {
    String paramsUri = String.join(",", urls);
    Map<String, Object> parameters = Map.of(
            "start", start.format(DATE_TIME_FORMATTER),
            "end", end.format(DATE_TIME_FORMATTER),
            "uris", paramsUri,
            "unique", unique);

    return (ResponseEntity<Object>) get(STATS_PATH + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters.size());
}
    public void saveStats(String app, String uri, String ip, LocalDateTime timestamp) {
        final EndpointHit endpointHit = new EndpointHit(app, uri, ip, timestamp);

        this.client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHit, EndpointHit.class)
                .retrieve()
                .toBodilessEntity()
                .doOnNext(c -> log.info("Save stats {}", endpointHit))
                .block();
    }
}