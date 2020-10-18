package com.example.holidayservice.controller;

import com.example.holidayservice.dto.HolidayResponseDto;
import com.example.holidayservice.exception.NextHolidayNotFoundException;
import com.example.holidayservice.service.HolidayService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web layer in application to processing requests.
 */
@RestController
@RequestMapping("${url.app.prefix}/${url.holiday.controller}")
@RequiredArgsConstructor
public class HolidayController {

  private static final String NEXT_HOLIDAY_NOT_FOUND =
      "Could not find the next holiday for given countries.";

  private final HolidayService holidayService;

  /**
   * Returns next holiday after the given date that will happen on the same day in both countries.
   * If the first country and second country code are the same just return next holiday in the
   * country. If in the country with this date exists a few holidays then merge holiday names.
   *
   * @param date         given date param
   * @param countryCode1 country1 code param
   * @param countryCode2 country2 code param
   * @return next holiday or throw exception when not found next holiday or get error from provider
   */
  @GetMapping("${url.holiday.controller.next-holiday}")
  @ResponseStatus(HttpStatus.OK)
  private HolidayResponseDto checkNextHoliday(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
      @RequestParam("country1") final String countryCode1,
      @RequestParam("country2") final String countryCode2) {
    holidayService.verifyAvailableCountries(countryCode1, countryCode2);
    Optional<HolidayResponseDto> nextHoliday = holidayService.findNextHolidayInBothCountries(
        date, countryCode1, countryCode2
    );
    return nextHoliday.orElseThrow(
        () -> new NextHolidayNotFoundException(NEXT_HOLIDAY_NOT_FOUND)
    );
  }

}
