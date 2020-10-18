package com.example.holidayservice.errorhandling;

import com.example.holidayservice.dto.CountryNotSupportedDto;
import com.example.holidayservice.dto.ErrorResponseDto;
import com.example.holidayservice.exception.HolidayServiceException;
import com.example.holidayservice.exception.NextHolidayNotFoundException;
import com.example.holidayservice.exception.CountryNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler in the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalErrorHandling {

  @ExceptionHandler(NextHolidayNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponseDto handleHolidayExceptions(NextHolidayNotFoundException ex) {
    log.error("HolidayNotFoundException: ", ex);
    return createGenericErrorResponse(ex.getMessage());
  }

  @ExceptionHandler(CountryNotSupportedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CountryNotSupportedDto handleNotSupportedException(CountryNotSupportedException ex) {
    log.error("CountryNotSupportedException: ", ex);
    return CountryNotSupportedDto.builder()
        .errorMessage(ex.getMessage())
        .supportedCountries(ex.getSupportedCountries())
        .build();
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleMissingParams(MissingServletRequestParameterException ex) {
    log.error("MissingServletRequestParameterException: ", ex);
    return createGenericErrorResponse(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    log.error("MethodArgumentTypeMismatchException: ", ex);
    return createGenericErrorResponse(ex.getMessage());
  }

  @ExceptionHandler(HolidayServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponseDto handleHolidayServiceException(HolidayServiceException ex) {
    log.error("HolidayServiceException: ", ex);
    return createGenericErrorResponse(ex.getMessage());
  }

  private ErrorResponseDto createGenericErrorResponse(String message) {
    return ErrorResponseDto.builder()
        .errorMessage(message)
        .build();
  }
}
