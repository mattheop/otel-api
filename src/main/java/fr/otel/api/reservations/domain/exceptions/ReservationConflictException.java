package fr.otel.api.reservations.domain.exceptions;

import fr.otel.api.reservations.domain.Reservation;

import java.util.List;

public class ReservationConflictException extends RuntimeException {
    private final Reservation failedReservation;
    private final List<Reservation> clashingReservations;

    public ReservationConflictException(Reservation failedReservation, List<Reservation> clashingReservations) {
        super("Reservation cannot be processed because one or more reservations exist on selected date range");

        this.failedReservation = failedReservation;
        this.clashingReservations = clashingReservations;
    }

    public Reservation getFailedReservation() {
        return failedReservation;
    }

    public List<Reservation> getClashingReservations() {
        return clashingReservations;
    }
}
