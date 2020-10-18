package com.example.holidayservice.exception;

/**
 * Exception when a next holiday in both countries not found.
 */
public class NextHolidayNotFoundException extends RuntimeException {

  public NextHolidayNotFoundException(String message) {
    super(message);
  }
}
