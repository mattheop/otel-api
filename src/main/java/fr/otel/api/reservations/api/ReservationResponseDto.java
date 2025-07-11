package fr.otel.api.reservations.api;

import fr.otel.api.customers.domain.Customer;
import fr.otel.api.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReservationResponseDto {
    private UUID id;
    private Customer customer;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
