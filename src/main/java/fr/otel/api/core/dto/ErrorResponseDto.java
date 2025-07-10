package fr.otel.api.core.dto;

import fr.otel.api.core.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    int httpStatus;
    String httpStatusText;
    String path;
    String errorCode;
    String message;
    ErrorType errorType;

    @Builder.Default
    OffsetDateTime timestamp = OffsetDateTime.now();
}
