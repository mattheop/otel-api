package fr.otel.api.reservations.domain;


import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ReservationFilters {
    private UUID id;
    private UUID customerId;
    private UUID roomId;
}