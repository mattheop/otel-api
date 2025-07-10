package fr.otel.api.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ValidationErrorResponseDto extends ErrorResponseDto {
    Map<String, String> validationErrors;
}
