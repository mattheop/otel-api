package fr.otel.api.rooms.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RoomResponseDto {
    private UUID id;
    private String roomNumber;
    private String roomType;
    private BigDecimal price;
}
