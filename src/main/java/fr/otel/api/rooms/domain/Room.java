package fr.otel.api.rooms.domain;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
public class Room {
    private UUID id;
    private String roomNumber;
    private String roomType;
    private BigDecimal price;
} 