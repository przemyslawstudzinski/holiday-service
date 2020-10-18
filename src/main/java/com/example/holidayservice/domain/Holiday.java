package com.example.holidayservice.domain;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

/**
 * Domain class to extract the date and local name of the holiday.
 */
@Data
@Builder
public class Holiday {

  private LocalDate date;

  private String localName;
}
