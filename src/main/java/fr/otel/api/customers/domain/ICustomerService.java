package fr.otel.api.customers.domain;

import fr.otel.api.customers.domain.exceptions.CustomerNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    List<Customer> fetchAllCustomers(int page, int size, String sortBy);
    Customer getCustomer(UUID customerId) throws CustomerNotFoundException;
    long countCustomers();
    Customer createCustomer(Customer customer);
    void deleteCustomer(UUID customerId) throws CustomerNotFoundException;
}
