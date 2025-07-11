package fr.otel.api.rooms.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID>, JpaSpecificationExecutor<RoomEntity> {
    Optional<RoomEntity> findByRoomNumber(String roomNumber);
    boolean existsByRoomNumber(String roomNumber);
} 