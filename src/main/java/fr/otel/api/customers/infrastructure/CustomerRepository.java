package fr.otel.api.customers.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    
    // Find customer by email (unique field)
    Optional<CustomerEntity> findByEmail(String email);
    
    // Check if customer exists by email
    boolean existsByEmail(String email);
    
    // Find customers by first name (case insensitive)
    List<CustomerEntity> findByFirstnameIgnoreCase(String firstname);
    
    // Find customers by last name (case insensitive)
    List<CustomerEntity> findByLastnameIgnoreCase(String lastname);
    
    // Find customers by phone
    Optional<CustomerEntity> findByPhone(String phone);
    
    // Custom query to search customers by name or email
    @Query("SELECT c FROM CustomerEntity c WHERE " +
           "LOWER(c.firstname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CustomerEntity> searchCustomers(@Param("searchTerm") String searchTerm);
}
