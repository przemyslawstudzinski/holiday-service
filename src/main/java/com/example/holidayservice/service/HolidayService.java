package com.example.holidayservice.service;

import com.example.holidayservice.domain.Country;
import com.example.holidayservice.domain.Holiday;
import com.example.holidayservice.dto.HolidayResponseDto;
import com.example.holidayservice.exception.CountryNotSupportedException;
import com.example.holidayservice.exception.HolidayServiceException;
import com.example.holidayservice.config.HolidayProviderProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for logic layer about holidays. Enables find next holiday in both countries and verify
 * country codes.
 */
@Service
@RequiredArgsConstructor
public class HolidayService {

  /**
   * Response messages.
   */
  private static final String COUNTRY_CODE = "Country code: ";
  private static final String NOT_SUPPORTED = " is not supported. ";
  private static final String ERROR_FROM_PROVIDER = "Internal error from the holiday's provider.";

  /**
   * REST Client to make requests to holiday provider.
   */
  private final RestTemplate restClient;

  /**
   * URLs in holiday provider.
   */
  private final HolidayProviderProperties properties;

  /**
   * Returns first next holiday response dto from both countries or empty.
   *
   * @param date         given date param
   * @param countryCode1 country1 code param
   * @param countryCode2 country2 code param
   * @return next holiday or empty
   */
  public Optional<HolidayResponseDto> findNextHolidayInBothCountries(final LocalDate date,
      final String countryCode1, final String countryCode2) {
    int year = date.getYear();
    // If next year also returns 0 match iterate for next years not make sense.
    while (year <= year + 1) {
      final var holidays1 = makeHolidaysRequest(properties.getHolidaysUrl(),
          countryCode1, year);
      final var country1Holidays = reduceHolidayCollection(date, holidays1);
      final var holidays2 = makeHolidaysRequest(properties.getHolidaysUrl(),
          countryCode2, year);
      final var country2Holidays = reduceHolidayCollection(date, holidays2);
      final var holiday = matchFirstHolidayInBothCollections(country1Holidays, country2Holidays);
      if (holiday.isPresent()) {
        return Optional.of(HolidayResponseDto.builder()
            .nextHolidayDate(holiday.get().getNextHolidayDate())
            .holidayName1(holiday.get().getHolidayName1())
            .holidayName2(holiday.get().getHolidayName2())
            .build());
      }
      year++;
    }
    return Optional.empty();
  }

  /**
   * Verifies country codes are available in the provider API.
   *
   * @param countryCode1 country1 code param
   * @param countryCode2 country2 code param
   */
  public void verifyAvailableCountries(final String countryCode1, final String countryCode2) {
    final var supportedCountries = makeAvailableCountriesRequest(
        properties.getAvailableCountriesUrl());
    final var keys = supportedCountries.stream()
        .map(Country::getKey)
        .collect(Collectors.toSet());
    StringBuilder errorMessage = new StringBuilder();
    errorMessage.append(checkCountryCodeExists(countryCode1, keys));
    errorMessage.append(checkCountryCodeExists(countryCode2, keys));
    if (!errorMessage.toString().isEmpty()) {
      throw new CountryNotSupportedException(errorMessage.toString(), supportedCountries);
    }
  }

  /**
   * Makes request to holiday API provider to get holiday list for a given country and year.
   *
   * @param url         request url
   * @param countryCode country code param
   * @param year        year from date
   * @return holidays collection in country
   */
  private Set<Holiday> makeHolidaysRequest(final String url,
      final String countryCode, final int year) {
    Holiday[] result;
    try {
      result = restClient.getForObject(
          url,
          Holiday[].class,
          year,
          countryCode
      );
    } catch (HttpStatusCodeException ex) {
      throw new HolidayServiceException(ERROR_FROM_PROVIDER);
    }
    return (result != null) ? Set.copyOf(Arrays.asList(result)) : Collections.emptySet();
  }

  /**
   * Removes holidays before and equal given date. If the holiday with this date exists a few then
   * merge holiday names.
   *
   * @param date     given date param
   * @param holidays holidays in country
   */
  private Set<Holiday> reduceHolidayCollection(final LocalDate date,
      final Collection<Holiday> holidays) {
    Set<Holiday> reduceHolidays = new HashSet<>();
    List<Holiday> sortedList = new ArrayList<>(holidays);
    sortedList.sort(Comparator.comparing(Holiday::getDate).thenComparing(Holiday::getLocalName));
    for (Holiday holiday : sortedList) {
      var holidayDateExists = reduceHolidays.stream()
          .filter(x -> x.getDate().equals(holiday.getDate()))
          .findFirst();
      if (holiday.getDate().isAfter(date) && holidayDateExists.isEmpty()) {
        reduceHolidays.add(holiday);
      } else if ((holiday.getDate().isAfter(date) && holidayDateExists.isPresent())) {
        var newHoliday = Holiday.builder()
            .date(holiday.getDate())
            .localName(holidayDateExists.get().getLocalName() + " | " + holiday.getLocalName())
            .build();
        reduceHolidays.remove(holidayDateExists.get());
        reduceHolidays.add(newHoliday);
      }
    }
    return reduceHolidays;
  }

  /**
   * Returns first next holiday DTO in both countries holiday collection.
   *
   * @param country1Holidays holidays in country1
   * @param country2Holidays holidays in country2
   * @return first match holiday DTO in both countries or empty
   */
  private Optional<HolidayResponseDto> matchFirstHolidayInBothCollections(
      final Collection<Holiday> country1Holidays, final Collection<Holiday> country2Holidays) {
    List<Holiday> mergedHolidays = new ArrayList<>();
    mergedHolidays.addAll(country1Holidays);
    mergedHolidays.addAll(country2Holidays);
    mergedHolidays.sort(Comparator.comparing(Holiday::getDate));
    if (mergedHolidays.size() <= 1) {
      return Optional.empty();
    }
    for (int i = 0; i < mergedHolidays.size() - 1; i++) {
      if (mergedHolidays.get(i).getDate().equals(mergedHolidays.get(i + 1).getDate())) {
        return Optional.of(HolidayResponseDto.builder()
            .nextHolidayDate(mergedHolidays.get(i).getDate())
            .holidayName1(mergedHolidays.get(i).getLocalName())
            .holidayName2(mergedHolidays.get(i + 1).getLocalName())
            .build());
      }
    }
    return Optional.empty();
  }

  /**
   * Makes request to holiday API provider to get available country codes.
   *
   * @param url available countries request url
   * @return country codes and names
   */
  private List<Country> makeAvailableCountriesRequest(final String url) {
    Country[] result;
    try {
      result = restClient.getForObject(
          url,
          Country[].class
      );
    } catch (HttpStatusCodeException ex) {
      throw new HolidayServiceException(ERROR_FROM_PROVIDER);
    }
    return (result != null) ? List.of(result) : Collections.emptyList();
  }

  /**
   * Verifies country code exists in provider API.
   *
   * @param countryCode country code param
   * @param keys        country codes collection
   * @return error message
   */
  private String checkCountryCodeExists(final String countryCode, final Collection<String> keys) {
    StringBuilder errorMessage = new StringBuilder();
    if (!keys.contains(countryCode.toUpperCase())) {
      errorMessage.append(COUNTRY_CODE);
      errorMessage.append(countryCode);
      errorMessage.append(NOT_SUPPORTED);
    }
    return errorMessage.toString();
  }

}
