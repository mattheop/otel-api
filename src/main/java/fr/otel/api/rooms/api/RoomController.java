package fr.otel.api.rooms.api;

import fr.otel.api.reservations.api.dtos.ReservationFindRequestDto;
import fr.otel.api.rooms.api.dtos.RoomFindRequestDto;
import fr.otel.api.rooms.api.dtos.RoomRequestDto;
import fr.otel.api.rooms.api.dtos.RoomResponseDto;
import fr.otel.api.rooms.application.RoomService;
import fr.otel.api.rooms.domain.IRoomService;
import fr.otel.api.rooms.domain.Room;
import fr.otel.api.core.dto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final IRoomService roomService;

    @GetMapping
    public PageResponseDto<RoomResponseDto> getRooms(
            @ModelAttribute @Valid RoomFindRequestDto reservationFindRequestDto) {

        List<RoomResponseDto> rooms = roomService.fetchAllRooms(reservationFindRequestDto.getPage(),
                        reservationFindRequestDto.getSize(),
                        reservationFindRequestDto.getSortBy(),
                        reservationFindRequestDto.getDirection(),
                        reservationFindRequestDto.getFrom(),
                        reservationFindRequestDto.getTo(),
                        reservationFindRequestDto.getIsAvailable()
                )
                .stream()
                .map(RoomMapper.INSTANCE::domainToResponseDto)
                .toList();

        long count = roomService.countRooms();
        return new PageResponseDto<>(rooms, reservationFindRequestDto.getPage(), reservationFindRequestDto.getSize(), count);
    }

    @GetMapping("/{uuid}")
    public RoomResponseDto getRoom(@PathVariable UUID uuid) {
        return RoomMapper.INSTANCE.domainToResponseDto(roomService.getRoom(uuid));
    }

    @PostMapping
    public RoomResponseDto createRoom(@RequestBody @Valid RoomRequestDto roomRequestDto) {
        Room room = RoomMapper.INSTANCE.requestToDomain(roomRequestDto);
        return RoomMapper.INSTANCE.domainToResponseDto(roomService.createRoom(room));
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("uuid") UUID roomId) {
        roomService.deleteRoom(roomId);
    }
} 