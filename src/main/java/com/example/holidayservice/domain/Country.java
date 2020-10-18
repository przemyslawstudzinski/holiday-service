package com.example.holidayservice.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Domain class to extract country code and name from json.
 */
@Data
@Builder
public class Country {

  private String key;

  private String value;
}
