package fr.otel.api.reservations.domain;

import fr.otel.api.customers.domain.Customer;
import fr.otel.api.rooms.domain.Room;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class Reservation {
    private UUID id;
    private Customer customer;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
