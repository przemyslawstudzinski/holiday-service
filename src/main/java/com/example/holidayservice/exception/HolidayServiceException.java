package com.example.holidayservice.exception;

/**
 * Generic exception for errors in the holiday service app.
 */
public class HolidayServiceException extends RuntimeException {

  public HolidayServiceException(String message) {
    super(message);
  }
}
