package com.example.holidayservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO response which next holiday date in both countries and names of holidays with local
 * languages.
 */
@Getter
@Builder
public class HolidayResponseDto {

  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate nextHolidayDate;

  private final String holidayName1;

  private final String holidayName2;

}
