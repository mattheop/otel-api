package fr.otel.api.reservations.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationFindRequestDto {
    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;

    @NotNull
    private String sortBy = "id";


    @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "direction must be 'asc' or 'desc'")
    private String direction = "asc";

    private UUID id;
    private UUID customerId;
    private UUID roomId;
}
