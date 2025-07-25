package fr.otel.api.rooms.api;

import fr.otel.api.rooms.api.dtos.RoomRequestDto;
import fr.otel.api.rooms.api.dtos.RoomResponseDto;
import fr.otel.api.rooms.domain.Room;
import fr.otel.api.rooms.infrastructure.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room entityToDomain(RoomEntity entity);
    @Mapping(target = "id", ignore = true)
    Room requestToDomain(RoomRequestDto roomRequestDto);
    RoomEntity domainToEntity(Room room);
    RoomResponseDto domainToResponseDto(Room room);
} 