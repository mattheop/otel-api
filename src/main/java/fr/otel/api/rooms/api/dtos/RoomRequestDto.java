package fr.otel.api.rooms.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomRequestDto {
    @NotBlank
    private String roomNumber;

    @NotBlank
    private String roomType;

    @NotNull
    private BigDecimal price;
} 