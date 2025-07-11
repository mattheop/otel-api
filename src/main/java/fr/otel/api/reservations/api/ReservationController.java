package fr.otel.api.reservations.api;

import fr.otel.api.reservations.domain.Reservation;
import fr.otel.api.reservations.domain.application.ReservationService;
import fr.otel.api.rooms.infrastructure.RoomEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationResponseDto> getReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return reservationService.findAll(page, size, sortBy)
                .stream()
                .map(ReservationMapper.INSTANCE::domainToResponseDto)
                .toList();
    }

    @GetMapping("/{uuid}")
    public ReservationResponseDto getReservation(@PathVariable UUID uuid) {
        return ReservationMapper.INSTANCE.domainToResponseDto(reservationService.getReservation(uuid));
    }

    @PostMapping
    public ReservationResponseDto createReservation(@RequestBody @Valid ReservationRequestDto reservationRequestDto) {
        Reservation createdReservation = reservationService.create(
                reservationRequestDto.getCustomerUUID(),
                reservationRequestDto.getRoomUUID(),
                reservationRequestDto.getStartDate(),
                reservationRequestDto.getEndDate(),
                reservationRequestDto.getNote()
        );

        return ReservationMapper.INSTANCE.domainToResponseDto(createdReservation);
    }
}
