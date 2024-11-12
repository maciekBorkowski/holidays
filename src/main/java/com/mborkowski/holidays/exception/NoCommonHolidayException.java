package com.mborkowski.holidays.exception;

public class NoCommonHolidayException extends RuntimeException {
    public NoCommonHolidayException(String message) {
        super(message);
    }
}
