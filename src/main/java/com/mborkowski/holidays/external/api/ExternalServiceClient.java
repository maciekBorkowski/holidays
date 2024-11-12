package com.mborkowski.holidays.external.api;

import com.mborkowski.holidays.external.api.dto.ExternalApiAvailableCountriesResponse;
import com.mborkowski.holidays.external.api.dto.ExternalApiHolidayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ExternalServiceClient {

    public Flux<ExternalApiHolidayResponse> getPublicHolidays(String url, String uri) {
        return WebClient.create(url).get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(ExternalApiHolidayResponse.class);
    }

    public Flux<ExternalApiAvailableCountriesResponse> getAvailableCountries(String url, String uri) {
        return WebClient.create(url).get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(ExternalApiAvailableCountriesResponse.class);
    }
}
