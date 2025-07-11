package fr.otel.api.customers.api;

import fr.otel.api.customers.domain.Customer;
import fr.otel.api.customers.infrastructure.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper( CustomerMapper.class );

    Customer entityToDomain(CustomerEntity entity);
    Customer requestToDomain(CustomerRequestDto customerRequestDto);

    CustomerEntity domainToEntity(Customer customer);
    CustomerResponseDto domainToResponseDto(Customer customer);
}
