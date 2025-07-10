package fr.otel.api.customers.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomerRequestDto {

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^0[1-9]\\d{8}$")
    private String phone;
}
