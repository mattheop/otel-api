package fr.otel.api.reservations.api;

import fr.otel.api.core.ErrorType;
import fr.otel.api.core.dto.ErrorResponseDto;
import fr.otel.api.customers.domain.exceptions.CustomerAlreadyExistException;
import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.reservations.domain.exceptions.ReservationConflictException;
import fr.otel.api.reservations.domain.exceptions.ReservationDateRangeInvalidException;
import fr.otel.api.reservations.domain.exceptions.ReservationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(ReservationConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleReservationConflict(ReservationConflictException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.CONFLICT.value())
                .httpStatusText(HttpStatus.CONFLICT.getReasonPhrase())
                .errorCode(ReservationConflictException.class.getSimpleName())
                .message("The reservation is conflicted with already confirmed reservations.")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleReservationNotFound(ReservationNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .httpStatusText(HttpStatus.NOT_FOUND.getReasonPhrase())
                .errorCode(ReservationNotFoundException.class.getSimpleName())
                .message("The reservation with UUID" + ex.getUuid() + " you provided does not exist.")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(ReservationDateRangeInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleReservationInvalidDateRangeException(ReservationDateRangeInvalidException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .httpStatusText(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode(ReservationDateRangeInvalidException.class.getSimpleName())
                .message(ex.getMessage())
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }
}
