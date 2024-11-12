package com.mborkowski.holidays.validators;

import com.mborkowski.holidays.utils.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class HolidayRequestValidatorTest {

    @InjectMocks
    private HolidayRequestValidator holidayRequestValidator;

    private static Stream<Arguments> provideCountryCodes() {
        return Stream.of(
            Arguments.of(List.of()),
            Arguments.of(List.of(Locale.CHINESE.getCountry())),
            Arguments.of(List.of(Locale.CHINESE.getCountry(), Locale.ENGLISH.getCountry(), Locale.FRENCH.getCountry())),
            Arguments.of(List.of(Locale.CHINESE.getCountry(), Locale.ENGLISH.getCountry(), Locale.FRENCH.getCountry(), Locale.GERMAN.getCountry()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideCountryCodes")
    void shouldThrowExceptionIfNumberOfCountryCodesIsNotEqualToTwo(final List<String> countryCodes) {
        assertThatThrownBy(() -> holidayRequestValidator.validateCountryCodes(countryCodes))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Number of country codes must be equal to 2");
    }

    @Test
    void shouldNotThrowExceptionIfNumberOfCountryCodesIsEqualToTwo() {
        assertThatCode(() -> holidayRequestValidator.validateCountryCodes(List.of(Locale.CHINESE.getCountry(), Locale.ENGLISH.getCountry())))
            .doesNotThrowAnyException();
    }
}