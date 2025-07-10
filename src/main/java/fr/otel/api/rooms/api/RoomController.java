package fr.otel.api.rooms.api;

import fr.otel.api.rooms.application.RoomService;
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
    private final RoomService roomService;

    @GetMapping
    public PageResponseDto<Room> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<Room> rooms = roomService.fetchAllRooms(page, size, sortBy);
        long count = roomService.countRooms();
        return new PageResponseDto<>(rooms, page, size, count);
    }

    @GetMapping("/{uuid}")
    public Room getRoom(@PathVariable UUID uuid) {
        return roomService.getRoom(uuid);
    }

    @PostMapping
    public Room createRoom(@RequestBody @Valid RoomRequestDto roomRequestDto) {
        Room room = RoomMapper.INSTANCE.requestToDomain(roomRequestDto);
        return roomService.createRoom(room);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("uuid") UUID roomId) {
        roomService.deleteRoom(roomId);
    }
} 