package com.example.holidayservice.config;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * URLs in holiday provider from properties file.
 */
@ConfigurationProperties("provider")
@ConstructorBinding
@Validated
@RequiredArgsConstructor
@Getter
public class HolidayProviderProperties {

  @NotBlank
  private final String holidaysUrl;

  @NotBlank
  private final String availableCountriesUrl;
}
