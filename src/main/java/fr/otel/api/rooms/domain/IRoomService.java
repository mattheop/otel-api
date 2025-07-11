package fr.otel.api.rooms.domain;

import fr.otel.api.rooms.domain.exceptions.RoomNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRoomService {
    List<Room> fetchAllRooms(int page, int size, String sortBy, String directionStr, LocalDate fromDate, LocalDate toDate, Boolean available);

    Room getRoom(UUID roomId) throws RoomNotFoundException;

    long countRooms();

    Room createRoom(Room room);

    void deleteRoom(UUID roomId) throws RoomNotFoundException;
} 