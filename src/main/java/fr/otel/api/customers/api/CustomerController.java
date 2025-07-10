package fr.otel.api.customers.api;

import fr.otel.api.customers.application.CustomerService;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.core.dto.PageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public PageResponseDto<Customer> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<Customer> customers = customerService.fetchAllCustomers(page, size, sortBy);
        long count = customerService.countCustomers();

        return new PageResponseDto<>(
                customers,
                page,
                size,
                count
        );
    }

    @GetMapping("/{uuid}")
    public Customer getCustomer(@PathVariable UUID uuid) {
        return customerService.getCustomer(uuid);
    }

    @PostMapping
    public Customer createCustomer(@RequestBody @Valid CustomerRequestDto customerRequestDto) {
        Customer customer = CustomerMapper.INSTANCE.requestToDomain(customerRequestDto);
        return customerService.createCustomer(customer);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable("uuid") UUID customerId) {
        customerService.deleteCustomer(customerId);
    }
}
