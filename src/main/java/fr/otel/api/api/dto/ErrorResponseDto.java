package fr.otel.api.api.dto;

import fr.otel.api.api.ErrorType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
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
