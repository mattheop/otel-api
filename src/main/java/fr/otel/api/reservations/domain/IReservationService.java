package fr.otel.api.reservations.domain;

import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.reservations.domain.exceptions.ReservationConflictException;
import fr.otel.api.reservations.domain.exceptions.ReservationLockAcquisitionException;
import fr.otel.api.reservations.domain.exceptions.ReservationNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IReservationService {
    List<Reservation> findAll(int page, int size, String orderBy, String directionStr, ReservationFilters filters);

    Reservation create(UUID customerId, UUID roomId, LocalDate startDate, LocalDate endDate, String note) throws ReservationConflictException, CustomerNotFoundException, ReservationNotFoundException, ReservationLockAcquisitionException;

    Reservation getReservation(UUID uuid) throws ReservationNotFoundException;

    long countReservations();
    long countReservations(ReservationFilters filters);
}
