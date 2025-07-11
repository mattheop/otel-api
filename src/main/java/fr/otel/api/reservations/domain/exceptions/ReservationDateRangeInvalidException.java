package fr.otel.api.reservations.domain.exceptions;

import java.time.OffsetDateTime;

public class ReservationDateRangeInvalidException extends RuntimeException {
    private final OffsetDateTime start;
    private final OffsetDateTime end;

    public ReservationDateRangeInvalidException(OffsetDateTime start, OffsetDateTime end) {
        super("Start date should be before end date, and minimum 1 night");
        this.start = start;
        this.end = end;
    }
}
