package fr.otel.api.reservations.api;

import fr.otel.api.customers.api.CustomerMapper;
import fr.otel.api.reservations.api.dtos.ReservationResponseDto;
import fr.otel.api.reservations.domain.Reservation;
import fr.otel.api.reservations.infrastructure.ReservationEntity;
import fr.otel.api.rooms.api.RoomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CustomerMapper.class, RoomMapper.class})
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    Reservation entityToDomain(ReservationEntity reservation);

    ReservationEntity domainToEntity(Reservation reservation);
    
    ReservationResponseDto domainToResponseDto(Reservation reservation);
}
