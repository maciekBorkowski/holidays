package com.mborkowski.holidays.external.api;

import com.mborkowski.holidays.external.api.dto.ExternalApiAvailableCountriesResponse;
import com.mborkowski.holidays.external.api.dto.ExternalApiHolidayResponse;

import java.util.List;

public interface ExternalHolidayApi {

    List<ExternalApiHolidayResponse> getHolidaysForGivenCountryCodeAndYear(String countryCode, int year);

    List<ExternalApiAvailableCountriesResponse> getAvailableCountryCodes();
}
