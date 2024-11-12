package com.mborkowski.holidays.config.errorhandling;

import com.mborkowski.holidays.exception.BadRequestException;
import com.mborkowski.holidays.exception.NoCommonHolidayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.text.MessageFormat;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentNotValidException(final DateTimeParseException ex) {
        log.error("Caught exception: {}", ex.getMessage());

        return new ResponseEntity<>(
            new ExceptionResponse("HOL400", "Given date is in wrong format"),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMissingArgumentException(final MissingServletRequestParameterException ex) {
        log.error("Caught exception: {}", ex.getMessage());
        return new ResponseEntity<>(
            new ExceptionResponse("HOL400", MessageFormat.format("Missing parameter: {0}", ex.getParameterName())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class, BadRequestException.class, IllegalStateException.class})
    @ResponseBody
    public ResponseEntity<Object> handleIllegalArgumentException(final RuntimeException ex) {
        log.error("Caught exception: {}", ex.getMessage());
        return new ResponseEntity<>(
            new ExceptionResponse("HOL400", ex.getMessage()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoCommonHolidayException.class)
    @ResponseBody
    public ResponseEntity<Object> handleNoCommonHolidayException(final NoCommonHolidayException ex) {
        return new ResponseEntity<>(
            new ExceptionResponse("HOL200", ex.getMessage()),
            HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(final Exception ex) {
        log.error("Caught unhandled exception: {}", ex.getMessage());
        return new ResponseEntity<>(
            new ExceptionResponse("HOL500", ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}