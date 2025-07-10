package fr.otel.api.rooms.api;

import fr.otel.api.core.ErrorType;
import fr.otel.api.core.dto.ErrorResponseDto;
import fr.otel.api.rooms.domain.exceptions.RoomAlreadyExistException;
import fr.otel.api.rooms.domain.exceptions.RoomNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoomExceptionHandler {
    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleRoomNotFound(RoomNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .httpStatusText(HttpStatus.NOT_FOUND.getReasonPhrase())
                .errorCode(RoomNotFoundException.class.getSimpleName())
                .message("The UUID " + ex.getUuid() + " you provided does not match any room.")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(RoomAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleRoomAlreadyExist(RoomAlreadyExistException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.CONFLICT.value())
                .httpStatusText(HttpStatus.CONFLICT.getReasonPhrase())
                .errorCode(RoomAlreadyExistException.class.getSimpleName())
                .message("The room number " + ex.getRoomNumber() + " you provided is already used.")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }
} 