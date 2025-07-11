package fr.otel.api.reservations.domain;

import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.reservations.domain.exceptions.ReservationConflictException;
import fr.otel.api.reservations.domain.exceptions.ReservationNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface IReservationService {
    List<Reservation> findAll(int page, int size, String orderBy);

    Reservation create(UUID customerId, UUID roomId, OffsetDateTime startDate, OffsetDateTime endDate, String note) throws ReservationConflictException, CustomerNotFoundException, ReservationNotFoundException;

    Reservation getReservation(UUID uuid) throws ReservationNotFoundException;
}
