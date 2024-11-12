package com.mborkowski.holidays.external.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalApiAvailableCountriesResponse {
    private String countryCode;
    private String name;
}
