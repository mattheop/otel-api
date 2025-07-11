package fr.otel.api.reservations.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID roomId, OffsetDateTime endDate, OffsetDateTime startDate);
}
