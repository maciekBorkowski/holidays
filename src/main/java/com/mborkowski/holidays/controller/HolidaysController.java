package com.mborkowski.holidays.controller;

import com.mborkowski.holidays.dto.HolidayResponse;
import com.mborkowski.holidays.service.HolidayService;
import com.mborkowski.holidays.validators.HolidayRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidaysController {

    private final HolidayRequestValidator holidayRequestValidator;
    private final HolidayService holidayService;

    @GetMapping
    public HolidayResponse getHolidays(
        @RequestParam(value = "countryCodes") List<String> countryCodes,
        @RequestParam(value = "date") LocalDate date) {
        holidayRequestValidator.validateCountryCodes(countryCodes);
        return holidayService.getNextCommonHolidays(countryCodes, date);
    }
}
