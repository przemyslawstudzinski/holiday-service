package com.example.holidayservice.dto;

import com.example.holidayservice.domain.Country;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO response when country code not supported.
 */
@Setter
@Getter
@Builder
public class CountryNotSupportedDto {

  private String errorMessage;

  private List<Country> supportedCountries;
}
