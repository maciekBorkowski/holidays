package com.mborkowski.holidays.config.errorhandling;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {
    private final String code;
    private final String errorMessage;
}
