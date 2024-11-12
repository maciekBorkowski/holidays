package com.mborkowski.holidays.service;

import com.google.common.collect.Sets;
import com.mborkowski.holidays.dto.HolidayResponse;
import com.mborkowski.holidays.exception.BadRequestException;
import com.mborkowski.holidays.exception.NoCommonHolidayException;
import com.mborkowski.holidays.external.api.ExternalHolidayApi;
import com.mborkowski.holidays.external.api.dto.ExternalApiAvailableCountriesResponse;
import com.mborkowski.holidays.external.api.dto.ExternalApiHolidayResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final List<ExternalHolidayApi> externalHolidayApis;

    @PostConstruct
    public void loadAvailableCountries() {
        externalHolidayApis.forEach(ExternalHolidayApi::getAvailableCountryCodes);
    }

    public HolidayResponse getNextCommonHolidays(final List<String> countryCodes, final LocalDate date) {
        final var holidaysForEachCountry = getHolidaysForEachCountry(countryCodes, date);
        return findCommonHolidays(date, holidaysForEachCountry);
    }

    private List<List<ExternalApiHolidayResponse>> getHolidaysForEachCountry(final List<String> countryCodes, final LocalDate date) {
        return countryCodes.stream()
            .map(countryCode -> getHolidaysForGivenCountryCodeAndYear(countryCode, date.getYear()))
            .toList();
    }

    private HolidayResponse findCommonHolidays(final LocalDate date, final List<List<ExternalApiHolidayResponse>> holidaysForEachCountry) {
        final var nextCommonHolidays = getNextCommonHolidays(date, holidaysForEachCountry);
        return HolidayResponse.builder()
            .name1(nextCommonHolidays.get(0).getLocalName())
            .name2(nextCommonHolidays.get(1).getLocalName())
            .date(nextCommonHolidays.get(0).getDate())
            .build();
    }

    private List<ExternalApiHolidayResponse> getNextCommonHolidays(final LocalDate date,
                                                                   final List<List<ExternalApiHolidayResponse>> holidaysForEachCountry) {

        final var nextCommonHolidayDate = getNextCommonHolidayDate(date, holidaysForEachCountry);
        return holidaysForEachCountry.stream()
            .map(holidays -> holidays.stream()
                .filter(holiday -> holiday.getDate().isEqual(nextCommonHolidayDate))
                .toList())
            .flatMap(Collection::stream)
            .toList();
    }

    private LocalDate getNextCommonHolidayDate(final LocalDate date, final List<List<ExternalApiHolidayResponse>> holidaysForEachCountry) {
        final var holidayDatesForEachCountry = holidaysForEachCountry.stream()
            .map(holidays -> holidays.stream()
                .map(ExternalApiHolidayResponse::getDate)
                .collect(Collectors.toSet()))
            .toList();

        final var commonHolidayDates = holidayDatesForEachCountry.stream()
            .reduce(Sets::intersection)
            .orElseThrow(() -> new NoCommonHolidayException("No common holidays found"));

        return commonHolidayDates.stream()
            .filter(foundDate -> foundDate.isAfter(date))
            .min(LocalDate::compareTo)
            .orElseThrow(() -> new NoCommonHolidayException("No common holidays found"));
    }

    private List<ExternalApiHolidayResponse> getHolidaysForGivenCountryCodeAndYear(final String countryCode, int year) {
        final var externalApi = externalHolidayApis.stream().findAny();
        if (externalApi.isEmpty()) {
            throw new IllegalStateException("No external API available");
        }
        final var externalApiClient = externalApi.get();
        validateIfCountryCodeIsAvailable(externalApiClient, countryCode);
        return externalApiClient.getHolidaysForGivenCountryCodeAndYear(countryCode, year);
    }

    private void validateIfCountryCodeIsAvailable(final ExternalHolidayApi externalApiClient, final String countryCode) {
        final var availableCountryCodes = externalApiClient.getAvailableCountryCodes().stream()
            .map(ExternalApiAvailableCountriesResponse::getCountryCode)
            .collect(Collectors.toSet());
        if (!availableCountryCodes.contains(countryCode)) {
            throw new BadRequestException("Country code is not available");
        }
    }
}
