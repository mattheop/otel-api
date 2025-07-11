package fr.otel.api.rooms.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomFindRequestDto {
    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;

    @NotNull
    private String sortBy = "id";


    @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "direction must be 'asc' or 'desc'")
    private String direction = "asc";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from = LocalDate.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to = LocalDate.now();

    private Boolean isAvailable;
}
