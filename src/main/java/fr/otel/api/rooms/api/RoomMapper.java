package fr.otel.api.rooms.api;

import fr.otel.api.rooms.domain.Room;
import fr.otel.api.rooms.infrastructure.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room entityToDomain(RoomEntity entity);
    Room requestToDomain(RoomRequestDto roomRequestDto);
    RoomEntity domainToEntity(Room room);
} 