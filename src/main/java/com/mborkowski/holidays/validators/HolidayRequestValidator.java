package com.mborkowski.holidays.validators;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HolidayRequestValidator {

    public void validateCountryCodes(final List<String> countryCodes) {
        validateCountryCodesSize(countryCodes);
    }

    private void validateCountryCodesSize(final List<String> countryCodes) {
        if (countryCodes.size() != 2) {
            throw new IllegalArgumentException("Number of country codes must be equal to 2");
        }
    }
}
