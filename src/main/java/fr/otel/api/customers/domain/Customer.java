package fr.otel.api.customers.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class Customer {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
