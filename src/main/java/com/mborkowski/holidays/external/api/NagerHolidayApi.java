package com.mborkowski.holidays.external.api;

import com.mborkowski.holidays.external.api.dto.ExternalApiAvailableCountriesResponse;
import com.mborkowski.holidays.external.api.dto.ExternalApiHolidayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NagerHolidayApi implements ExternalHolidayApi {

    @Value("${nager.holiday.api.url}")
    private String baseUrl;

    private final ExternalServiceClient externalServiceClient;

    @Cacheable(cacheNames = "holidaysNager", key = "#countryCode + #year")
    @Override
    public List<ExternalApiHolidayResponse> getHolidaysForGivenCountryCodeAndYear(final String countryCode, final int year) {
        final var uri = "api/v3/publicholidays/" + "/" + year + "/" + countryCode;
        final var responseFlux = externalServiceClient.getPublicHolidays(baseUrl, uri);
        return responseFlux.collectList().block();
    }

    @Cacheable(cacheNames = "availableCountriesNager")
    @Override
    public List<ExternalApiAvailableCountriesResponse> getAvailableCountryCodes() {
        final var uri = "api/v3/availablecountries";
        final var responseFlux = externalServiceClient.getAvailableCountries(baseUrl, uri);
        final var response = responseFlux.collectList().block();
        if (response == null) {
            throw new IllegalStateException("No response from external API");
        }
        return response;
    }
}
