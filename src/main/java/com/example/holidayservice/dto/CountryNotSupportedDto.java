package com.example.holidayservice.dto;

import com.example.holidayservice.domain.Country;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO response when country code not supported.
 */
@Getter
@Builder
public class CountryNotSupportedDto {

  private final String errorMessage;

  private final List<Country> supportedCountries;
}
