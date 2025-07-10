package fr.otel.api.customers.api;

import fr.otel.api.api.dto.ErrorResponseDto;
import fr.otel.api.api.ErrorType;
import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .httpStatusText(HttpStatus.NOT_FOUND.getReasonPhrase())
                .errorCode(CustomerNotFoundException.class.getSimpleName())
                .message("The UUID " + ex.getUuid() + " you provided does not match any customer.")
                .errorType(ErrorType.BUSINESS)
                .path(request.getRequestURI())
                .build();
    }
}
