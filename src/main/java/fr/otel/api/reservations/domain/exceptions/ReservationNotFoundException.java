package fr.otel.api.reservations.domain.exceptions;

import java.util.UUID;

public class ReservationNotFoundException extends RuntimeException {

    private final String uuid;

    public ReservationNotFoundException(UUID uuid, Throwable cause) {
        super("Reservation with ID " + uuid + " not found.", cause);
        this.uuid = String.valueOf(uuid);
    }

    public ReservationNotFoundException(String uuid, Throwable cause) {
        super("Reservation with ID " + uuid + " not found.", cause);
        this.uuid = uuid;
    }

    public ReservationNotFoundException(UUID uuid) {
        super("Reservation with ID " + uuid + " not found.");
        this.uuid = String.valueOf(uuid);
    }

    public ReservationNotFoundException(String uuid) {
        super("Reservation with ID " + uuid + " not found.");
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
