package fr.otel.api.customers.api;

import fr.otel.api.customers.domain.Customer;
import fr.otel.api.rooms.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CustomerResponseDto {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
