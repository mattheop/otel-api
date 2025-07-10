package fr.otel.api.rooms.application;

import fr.otel.api.rooms.api.RoomMapper;
import fr.otel.api.rooms.domain.Room;
import fr.otel.api.rooms.domain.IRoomService;
import fr.otel.api.rooms.domain.exceptions.RoomAlreadyExistException;
import fr.otel.api.rooms.domain.exceptions.RoomNotFoundException;
import fr.otel.api.rooms.infrastructure.RoomEntity;
import fr.otel.api.rooms.infrastructure.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;

    public List<Room> fetchAllRooms(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return roomRepository.findAll(pageable).stream()
                .map(RoomMapper.INSTANCE::entityToDomain)
                .toList();
    }

    public long countRooms() {
        return roomRepository.count();
    }

    public Room getRoom(UUID roomId) throws RoomNotFoundException {
        return roomRepository.findById(roomId)
                .map(RoomMapper.INSTANCE::entityToDomain)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    public Room createRoom(Room room) {
        if (roomRepository.findByRoomNumber(room.getRoomNumber()).isPresent()) {
            throw new RoomAlreadyExistException(room.getRoomNumber());
        }
        RoomEntity roomEntity = RoomMapper.INSTANCE.domainToEntity(room);
        RoomEntity savedEntity = roomRepository.save(roomEntity);
        return RoomMapper.INSTANCE.entityToDomain(savedEntity);
    }

    public void deleteRoom(UUID roomId) throws RoomNotFoundException {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        roomRepository.deleteById(roomId);
    }
} 