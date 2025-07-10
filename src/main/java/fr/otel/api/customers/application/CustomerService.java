package fr.otel.api.customers.application;

import fr.otel.api.customers.api.CustomerMapper;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.customers.domain.ICustomerService;
import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;
import fr.otel.api.customers.infrastructure.CustomerEntity;
import fr.otel.api.customers.infrastructure.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> fetchAllCustomers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return customerRepository.findAll(pageable).stream()
                .map(CustomerMapper.INSTANCE::entityToDomain)
                .toList();
    }

    public long countCustomers() {
        return customerRepository.count();
    }

    public Customer getCustomer(UUID customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .map(CustomerMapper.INSTANCE::entityToDomain)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Customer createCustomer(Customer customer) {
        CustomerEntity customerEntity = CustomerMapper.INSTANCE.domainToEntity(customer);
        CustomerEntity savedEntity = customerRepository.save(customerEntity);

        return CustomerMapper.INSTANCE.entityToDomain(savedEntity);
    }

    public void deleteCustomer(UUID customerId) throws CustomerNotFoundException {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        customerRepository.deleteById(customerId);
    }
}
