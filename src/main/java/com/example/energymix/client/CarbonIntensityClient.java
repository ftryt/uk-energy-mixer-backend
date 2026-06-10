package com.example.energymix.client;

import com.example.energymix.dto.external.CarbonIntensityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class CarbonIntensityClient {

    private final RestClient restClient;

    // The constructor configures RestClient with the base URL of the UK API
    public CarbonIntensityClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.carbonintensity.org.uk")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public CarbonIntensityResponse getGenerationMix(ZonedDateTime from, ZonedDateTime to) {

        String fromIso = from.truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ISO_INSTANT);
        String toIso = to.truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ISO_INSTANT);

        System.out.println(fromIso);
        System.out.println(toIso);

        return restClient.get()
                .uri("/generation/" + fromIso + "/" + toIso)
                .retrieve()
                .body(CarbonIntensityResponse.class); // Automatic JSON -> Java Record conversion
    }
}
