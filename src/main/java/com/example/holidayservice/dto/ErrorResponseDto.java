package com.example.holidayservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic DTO response when error.
 */
@Getter
@Setter
@Builder
public class ErrorResponseDto {

  private String errorMessage;

}
