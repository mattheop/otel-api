package fr.otel.api.reservations.domain.application;

import fr.otel.api.customers.application.CustomerService;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.reservations.api.ReservationMapper;
import fr.otel.api.reservations.domain.IReservationService;
import fr.otel.api.reservations.domain.Reservation;
import fr.otel.api.reservations.domain.exceptions.ReservationConflictException;
import fr.otel.api.reservations.domain.exceptions.ReservationDateRangeInvalidException;
import fr.otel.api.reservations.domain.exceptions.ReservationNotFoundException;
import fr.otel.api.reservations.infrastructure.ReservationEntity;
import fr.otel.api.reservations.infrastructure.ReservationRepository;
import fr.otel.api.rooms.domain.IRoomService;
import fr.otel.api.rooms.domain.Room;
import fr.otel.api.rooms.domain.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final IRoomService roomService;
    private final CustomerService customerService;

    @Override
    public List<Reservation> findAll(int page, int size, String orderBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return reservationRepository.findAll(pageable).stream()
                .map(ReservationMapper.INSTANCE::entityToDomain)
                .toList();
    }

    @Override
    public Reservation create(UUID customerId, UUID roomId, LocalDate startDate, LocalDate endDate, String note) throws ReservationConflictException, RoomNotFoundException, CustomerNotFoundException {
        if (!isValidDateRange(startDate, endDate)) {
            throw new ReservationDateRangeInvalidException(startDate, endDate);
        }

        Room room = roomService.getRoom(roomId);
        Customer customer = customerService.getCustomer(customerId);

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .room(room)
                .startDate(startDate)
                .endDate(endDate)
                .note(note)
                .build();

        List<Reservation> existingReservations = reservationRepository.findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(room.getId(), startDate, endDate)
                .stream()
                .map(ReservationMapper.INSTANCE::entityToDomain)
                .toList();

        if (!existingReservations.isEmpty()) {
            throw new ReservationConflictException(reservation, existingReservations);
        }

        ReservationEntity savedReservation = reservationRepository.save(ReservationMapper.INSTANCE.domainToEntity(reservation));
        return ReservationMapper.INSTANCE.entityToDomain(savedReservation);
    }

    @Override
    public Reservation getReservation(UUID uuid) throws ReservationNotFoundException {
        Optional<ReservationEntity> reservation = reservationRepository.findById(uuid);
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException(uuid);
        }

        return ReservationMapper.INSTANCE.entityToDomain(reservation.get());
    }

    @Override
    public long countReservations() {
        return reservationRepository.count();
    }

    private boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        if (startDate.isAfter(endDate)) {
            return false;
        }

        return endDate.isAfter(startDate.plusDays(1));
    }
}
