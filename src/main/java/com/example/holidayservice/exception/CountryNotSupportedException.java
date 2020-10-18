package com.example.holidayservice.exception;

import com.example.holidayservice.domain.Country;
import java.util.List;
import lombok.Getter;

/**
 * Exception when a country code not supported.
 */
public class CountryNotSupportedException extends RuntimeException {

  @Getter
  private final List<Country> supportedCountries;

  public CountryNotSupportedException(String message, List<Country> supportedCountries) {
    super(message);
    this.supportedCountries = supportedCountries;
  }
}
