package fr.otel.api.reservations.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID roomId, LocalDate endDate, LocalDate startDate);
}
