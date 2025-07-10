package fr.otel.api.core;

import fr.otel.api.core.dto.ValidationErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CoreExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponseDto handleCustomerNotFound(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldsErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldsErrors.put(error.getField(), error.getDefaultMessage())
        );

        return ValidationErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .httpStatusText(HttpStatus.NOT_FOUND.getReasonPhrase())
                .errorCode("RequestObjectNotValidException")
                .message("The requested object is invalid")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .validationErrors(fieldsErrors)
                .build();
    }
}
