package fr.otel.api.reservations.domain.exceptions;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ReservationDateRangeInvalidException extends RuntimeException {
    private final LocalDate start;
    private final LocalDate end;

    public ReservationDateRangeInvalidException(LocalDate start, LocalDate end) {
        super("Start date should be before end date, and minimum 1 night");
        this.start = start;
        this.end = end;
    }
}
