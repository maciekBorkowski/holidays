package com.mborkowski.holidays.service;

import com.mborkowski.holidays.exception.BadRequestException;
import com.mborkowski.holidays.exception.NoCommonHolidayException;
import com.mborkowski.holidays.external.api.ExternalHolidayApi;
import com.mborkowski.holidays.external.api.NagerHolidayApi;
import com.mborkowski.holidays.external.api.dto.ExternalApiAvailableCountriesResponse;
import com.mborkowski.holidays.external.api.dto.ExternalApiHolidayResponse;
import com.mborkowski.holidays.utils.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class HolidayServiceTest {

    @InjectMocks
    private HolidayService holidayService;

    private <T extends ExternalHolidayApi> void assignMockToExternalHolidayApis(final T externalHolidayApi) {
        final var existingApisOptionally = Optional.ofNullable((List <ExternalHolidayApi >) ReflectionTestUtils.getField(holidayService, "externalHolidayApis"));

        existingApisOptionally.ifPresentOrElse(
            existingApis -> addExternalHolidayApi(externalHolidayApi, existingApis),
            () -> {
                final var existingApis = new ArrayList<ExternalHolidayApi>();
                addExternalHolidayApi(externalHolidayApi, existingApis);
            }
        );
    }

    private void addExternalHolidayApi(final ExternalHolidayApi externalHolidayApi, final List<ExternalHolidayApi> existingApis) {
        existingApis.add(externalHolidayApi);
        ReflectionTestUtils.setField(holidayService, "externalHolidayApis", existingApis);
    }

    @Test
    void whenApiListIsEmptyThenExceptionShouldBeThrown() {
        ReflectionTestUtils.setField(holidayService, "externalHolidayApis", new LinkedList<ExternalHolidayApi>());

        assertThatThrownBy(() -> holidayService.getNextCommonHolidays(List.of("PL", "DE"), LocalDate.now()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No external API available");
    }

    @Test
    void whenAnyOfRequestedCountryCodeIsNotAvailableThenExceptionShouldBeThrown() {
        final var nagerHolidayApi = mock(NagerHolidayApi.class);
        when(nagerHolidayApi.getAvailableCountryCodes()).thenReturn(List.of(
            ExternalApiAvailableCountriesResponse.builder().countryCode("DE").name("Deutschland").build()
        ));
        assignMockToExternalHolidayApis(nagerHolidayApi);

        assertThatThrownBy(() -> holidayService.getNextCommonHolidays(List.of("PL", "CZ"), LocalDate.now()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Country code is not available");

        assertThatThrownBy(() -> holidayService.getNextCommonHolidays(List.of("CZ", "PL"), LocalDate.now()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Country code is not available");
    }

    @Test
    void shouldThrowExceptionIfNotCommonHolidaysWereFound() {
        final var nagerHolidayApi = mock(NagerHolidayApi.class);
        when(nagerHolidayApi.getAvailableCountryCodes()).thenReturn(List.of(
            ExternalApiAvailableCountriesResponse.builder().countryCode("PL").name("Polska").build(),
            ExternalApiAvailableCountriesResponse.builder().countryCode("DE").name("Deutschland").build()
        ));
        when(nagerHolidayApi.getHolidaysForGivenCountryCodeAndYear("PL", 2021)).thenReturn(List.of(
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 1, 1)).localName("Nowy Rok").build()
        ));
        when(nagerHolidayApi.getHolidaysForGivenCountryCodeAndYear("DE", 2021)).thenReturn(List.of(
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 1, 6)).localName("Heilige Drei Könige").build()
        ));
        assignMockToExternalHolidayApis(nagerHolidayApi);

        assertThatThrownBy(() -> holidayService.getNextCommonHolidays(List.of("PL", "DE"), LocalDate.now()))
            .isInstanceOf(NoCommonHolidayException.class)
            .hasMessage("No common holidays found");
    }

    @Test
    void shouldReturnNextCommonHoliday() {
        final var nagerHolidayApi = mock(NagerHolidayApi.class);
        when(nagerHolidayApi.getAvailableCountryCodes()).thenReturn(List.of(
            ExternalApiAvailableCountriesResponse.builder().countryCode("PL").name("Polska").build(),
            ExternalApiAvailableCountriesResponse.builder().countryCode("DE").name("Deutschland").build(),
            ExternalApiAvailableCountriesResponse.builder().countryCode("AT").name("Austria").build()
        ));
        when(nagerHolidayApi.getHolidaysForGivenCountryCodeAndYear("PL", 2021)).thenReturn(List.of(
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 1, 1)).localName("Nowy Rok").build(),
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 1, 6)).localName("Trzech Króli").build(),
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 11, 1)).localName("Wszystkich Świętych").build(),
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 12, 25)).localName("Boże Narodzenie").build()

        ));
        when(nagerHolidayApi.getHolidaysForGivenCountryCodeAndYear("DE", 2021)).thenReturn(List.of(
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 1, 6)).localName("Heilige Drei Könige").build(),
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 12, 25)).localName("Erster Weihnachtstag").build()
        ));

        when(nagerHolidayApi.getHolidaysForGivenCountryCodeAndYear("AT", 2021)).thenReturn(List.of(
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 6, 3)).localName("Fronleichnam").build(),
            ExternalApiHolidayResponse.builder().date(LocalDate.of(2021, 12, 25)).localName("Weihnachten").build()
        ));
        assignMockToExternalHolidayApis(nagerHolidayApi);

        final var plAndDeCommonHolidayAfterFirstOfJanuary = holidayService.getNextCommonHolidays(List.of("PL", "DE"), LocalDate.of(2021, 1, 1));
        final var plAndDeCommonHolidayAfterFirstOfDecember = holidayService.getNextCommonHolidays(List.of("PL", "DE"), LocalDate.of(2021, 12, 1));

        assertThat(plAndDeCommonHolidayAfterFirstOfJanuary.getDate()).isEqualTo(LocalDate.of(2021, 1, 6));
        assertThat(plAndDeCommonHolidayAfterFirstOfJanuary.getName1()).isEqualTo("Trzech Króli");
        assertThat(plAndDeCommonHolidayAfterFirstOfJanuary.getName2()).isEqualTo("Heilige Drei Könige");

        assertThat(plAndDeCommonHolidayAfterFirstOfDecember.getDate()).isEqualTo(LocalDate.of(2021, 12, 25));
        assertThat(plAndDeCommonHolidayAfterFirstOfDecember.getName1()).isEqualTo("Boże Narodzenie");
        assertThat(plAndDeCommonHolidayAfterFirstOfDecember.getName2()).isEqualTo("Erster Weihnachtstag");

        final var deAndAtCommonHolidayAfterFirstOfMarch = holidayService.getNextCommonHolidays(List.of("DE", "AT"), LocalDate.of(2021, 3, 1));
        final var deAndAtCommonHolidayAfterFirstOfDecember = holidayService.getNextCommonHolidays(List.of("DE", "AT"), LocalDate.of(2021, 12, 1));

        assertThat(deAndAtCommonHolidayAfterFirstOfMarch.getDate()).isEqualTo(LocalDate.of(2021, 12, 25));
        assertThat(deAndAtCommonHolidayAfterFirstOfMarch.getName1()).isEqualTo("Erster Weihnachtstag");
        assertThat(deAndAtCommonHolidayAfterFirstOfMarch.getName2()).isEqualTo("Weihnachten");

        assertThat(deAndAtCommonHolidayAfterFirstOfDecember.getDate()).isEqualTo(LocalDate.of(2021, 12, 25));
        assertThat(deAndAtCommonHolidayAfterFirstOfDecember.getName1()).isEqualTo("Erster Weihnachtstag");
        assertThat(deAndAtCommonHolidayAfterFirstOfDecember.getName2()).isEqualTo("Weihnachten");

        final var atAndPlCommonHolidayAfterFirstOfMarch = holidayService.getNextCommonHolidays(List.of("AT", "PL"), LocalDate.of(2021, 3, 1));
        final var atAndPlCommonHolidayAfterFirstOfDecember = holidayService.getNextCommonHolidays(List.of("AT", "PL"), LocalDate.of(2021, 12, 1));

        assertThat(atAndPlCommonHolidayAfterFirstOfMarch.getDate()).isEqualTo(LocalDate.of(2021, 12, 25));
        assertThat(atAndPlCommonHolidayAfterFirstOfMarch.getName1()).isEqualTo("Weihnachten");
        assertThat(atAndPlCommonHolidayAfterFirstOfMarch.getName2()).isEqualTo("Boże Narodzenie");

        assertThat(atAndPlCommonHolidayAfterFirstOfDecember.getDate()).isEqualTo(LocalDate.of(2021, 12, 25));
        assertThat(atAndPlCommonHolidayAfterFirstOfDecember.getName1()).isEqualTo("Weihnachten");
        assertThat(atAndPlCommonHolidayAfterFirstOfDecember.getName2()).isEqualTo("Boże Narodzenie");

    }
}