package com.mborkowski.holidays.controller;

import com.mborkowski.holidays.utils.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class HolidaysControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCorrectErrorIfDateIsGivenInWrongFormat() throws Exception {
        mockMvc.perform(get("/holidays?date=01-01-2024&countryCodes=PL,DE"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value("Given date is in wrong format"))
            .andExpect(jsonPath("$.code").value("HOL400"));
    }

    @Test
    void shouldReturnCorrectErrorIfDateIsNotGiven() throws Exception {
        mockMvc.perform(get("/holidays?countryCodes=PL,DE"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value("Missing parameter: date"))
            .andExpect(jsonPath("$.code").value("HOL400"));
    }

    @Test
    void shouldReturnCorrectErrorIfCountryCodesAreNotGiven() throws Exception {
        mockMvc.perform(get("/holidays?date=2024-01-01"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value("Missing parameter: countryCodes"))
            .andExpect(jsonPath("$.code").value("HOL400"));
    }
}