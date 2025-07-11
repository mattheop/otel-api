package fr.otel.api.reservations.api;

import fr.otel.api.core.dto.PageResponseDto;
import fr.otel.api.reservations.api.dtos.ReservationFindRequestDto;
import fr.otel.api.reservations.api.dtos.ReservationRequestDto;
import fr.otel.api.reservations.api.dtos.ReservationResponseDto;
import fr.otel.api.reservations.domain.Reservation;
import fr.otel.api.reservations.application.ReservationService;
import fr.otel.api.reservations.domain.ReservationFilters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public PageResponseDto<ReservationResponseDto> getReservations(
            @ModelAttribute @Valid ReservationFindRequestDto request) {
        ReservationFilters reservationFilters = ReservationFilters.builder()
                .customerId(request.getCustomerId())
                .roomId(request.getRoomId())
                .id(request.getId())
                .build();

        List<ReservationResponseDto> reservationResponseDtos = reservationService.findAll(request.getPage(),
                        request.getSize(),
                        request.getSortBy(),
                        request.getDirection(),
                        reservationFilters)
                .stream()
                .map(ReservationMapper.INSTANCE::domainToResponseDto)
                .toList();

        long totalCount = reservationService.countReservations(reservationFilters);

        return new PageResponseDto<>(reservationResponseDtos, request.getPage(), request.getSize(), totalCount);
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

    @PutMapping("/{uuid}")
    public ReservationResponseDto updateReservation(@PathVariable UUID uuid, @RequestBody @Valid ReservationRequestDto reservationRequestDto) {
        Reservation updatedReservation = reservationService.update(
                uuid,
                reservationRequestDto.getCustomerUUID(),
                reservationRequestDto.getRoomUUID(),
                reservationRequestDto.getStartDate(),
                reservationRequestDto.getEndDate(),
                reservationRequestDto.getNote()
        );
        return ReservationMapper.INSTANCE.domainToResponseDto(updatedReservation);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable UUID uuid) {
        reservationService.deleteReservation(uuid);
    }
}
