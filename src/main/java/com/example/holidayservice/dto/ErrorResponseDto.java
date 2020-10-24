package com.example.holidayservice.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Generic DTO response when error.
 */
@Getter
@Builder
public class ErrorResponseDto {

  private final String errorMessage;

}
