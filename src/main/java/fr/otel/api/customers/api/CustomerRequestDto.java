package fr.otel.api.customers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomerRequestDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
