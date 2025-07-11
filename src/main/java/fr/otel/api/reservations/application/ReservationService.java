package fr.otel.api.reservations.application;

import fr.otel.api.customers.application.CustomerService;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.reservations.api.ReservationMapper;
import fr.otel.api.reservations.domain.IReservationService;
import fr.otel.api.reservations.domain.Reservation;
import fr.otel.api.reservations.domain.ReservationFilters;
import fr.otel.api.reservations.domain.exceptions.ReservationConflictException;
import fr.otel.api.reservations.domain.exceptions.ReservationDateRangeInvalidException;
import fr.otel.api.reservations.domain.exceptions.ReservationLockAcquisitionException;
import fr.otel.api.reservations.domain.exceptions.ReservationNotFoundException;
import fr.otel.api.reservations.infrastructure.ReservationEntity;
import fr.otel.api.reservations.infrastructure.ReservationRepository;
import fr.otel.api.reservations.infrastructure.ReservationSpecification;
import fr.otel.api.rooms.domain.IRoomService;
import fr.otel.api.rooms.domain.Room;
import fr.otel.api.rooms.domain.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import fr.otel.api.customers.api.CustomerMapper;
import fr.otel.api.rooms.api.RoomMapper;

@RequiredArgsConstructor
@Service
public class ReservationService implements IReservationService {

    private static final int MAX_LOCK_WAIT_TIME_IN_SECONDS = 10;
    private static final int LOCK_TIMEOUT_IN_SECONDS = 30;

    private final ReservationRepository reservationRepository;
    private final IRoomService roomService;
    private final CustomerService customerService;
    private final RedissonClient redissonClient;

    @Override
    public List<Reservation> findAll(int page, int size, String orderBy, String directionStr, ReservationFilters filters) {
        Sort.Direction direction = Sort.Direction.fromString(directionStr);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));

        Specification<ReservationEntity> reservationSpecification = ReservationSpecification.buildSpec(
                filters.getId(),
                filters.getCustomerId(),
                filters.getRoomId()
        );

        return reservationRepository.findAll(reservationSpecification, pageable).stream()
                .map(ReservationMapper.INSTANCE::entityToDomain)
                .toList();
    }

    public Reservation create(UUID customerId, UUID roomId, LocalDate startDate, LocalDate endDate, String note) {
        RLock lock = redissonClient.getLock("lock:room:" + roomId);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(MAX_LOCK_WAIT_TIME_IN_SECONDS, LOCK_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                throw new ReservationLockAcquisitionException();
            }

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

            List<Reservation> existingReservations = reservationRepository
                    .findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(room.getId(), startDate, endDate)
                    .stream()
                    .map(ReservationMapper.INSTANCE::entityToDomain)
                    .toList();

            if (!existingReservations.isEmpty()) {
                throw new ReservationConflictException(reservation, existingReservations);
            }

            ReservationEntity savedReservation = reservationRepository.save(ReservationMapper.INSTANCE.domainToEntity(reservation));
            return ReservationMapper.INSTANCE.entityToDomain(savedReservation);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReservationLockAcquisitionException(e);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public Reservation update(UUID reservationId, UUID customerId, UUID roomId, LocalDate startDate, LocalDate endDate, String note) {
        // Fetch the existing reservation
        ReservationEntity existingEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        UUID oldRoomId = existingEntity.getRoom().getId();
        UUID lockRoomId = roomId != null ? roomId : oldRoomId;
        RLock lock = redissonClient.getLock("lock:room:" + lockRoomId);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(MAX_LOCK_WAIT_TIME_IN_SECONDS, LOCK_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                throw new ReservationLockAcquisitionException();
            }

            if (!isValidDateRange(startDate, endDate)) {
                throw new ReservationDateRangeInvalidException(startDate, endDate);
            }

            Room room = roomService.getRoom(roomId);
            Customer customer = customerService.getCustomer(customerId);

            // Check for conflicts, excluding the current reservation
            List<Reservation> existingReservations = reservationRepository
                    .findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(room.getId(), startDate, endDate)
                    .stream()
                    .filter(e -> !e.getId().equals(reservationId))
                    .map(ReservationMapper.INSTANCE::entityToDomain)
                    .toList();

            Reservation updatedReservation = Reservation.builder()
                    .id(reservationId)
                    .customer(customer)
                    .room(room)
                    .startDate(startDate)
                    .endDate(endDate)
                    .note(note)
                    .createdAt(existingEntity.getCreatedAt()) // will be set by JPA
                    .build();

            if (!existingReservations.isEmpty()) {
                throw new ReservationConflictException(updatedReservation, existingReservations);
            }

            // Update entity fields
            existingEntity.setCustomer(CustomerMapper.INSTANCE.domainToEntity(customer));
            existingEntity.setRoom(RoomMapper.INSTANCE.domainToEntity(room));
            existingEntity.setStartDate(startDate);
            existingEntity.setEndDate(endDate);
            existingEntity.setNote(note);
            ReservationEntity saved = reservationRepository.save(existingEntity);
            return ReservationMapper.INSTANCE.entityToDomain(saved);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReservationLockAcquisitionException(e);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
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

    @Override
    public long countReservations(ReservationFilters filters) {
        Specification<ReservationEntity> reservationSpecification = ReservationSpecification.buildSpec(
                filters.getId(),
                filters.getCustomerId(),
                filters.getRoomId()
        );

        return reservationRepository.count(reservationSpecification);
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

    @Override
    public void deleteReservation(UUID uuid) {
        if (!reservationRepository.existsById(uuid)) {
            throw new ReservationNotFoundException(uuid);
        }
        reservationRepository.deleteById(uuid);
    }
}
