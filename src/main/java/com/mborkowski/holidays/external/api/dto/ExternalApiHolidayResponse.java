package com.mborkowski.holidays.external.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalApiHolidayResponse {
    private LocalDate date;
    private String localName;
}
