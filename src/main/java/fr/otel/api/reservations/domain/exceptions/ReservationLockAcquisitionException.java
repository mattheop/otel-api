package fr.otel.api.reservations.domain.exceptions;

import fr.otel.api.reservations.domain.Reservation;

import java.util.List;

public class ReservationLockAcquisitionException extends RuntimeException {

    private static final String message = "Reservation cannot be processed because lock on room cannot be acquired. " +
                                          "It can be due of too many reservations requests on this room. Try again later.";

    public ReservationLockAcquisitionException(Throwable cause) {
        super(message, cause);
    }

    public ReservationLockAcquisitionException() {
        super(message);
    }
}
