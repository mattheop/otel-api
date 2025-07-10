package fr.otel.api.integration;

import fr.otel.api.core.dto.PageResponseDto;
import fr.otel.api.core.dto.ValidationErrorResponseDto;
import fr.otel.api.customers.api.CustomerRequestDto;
import fr.otel.api.customers.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createAndGetCustomer() {
        CustomerRequestDto request = new CustomerRequestDto("John", "Doe", "john.doe@email.com", "0123456789");
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(baseUrl + "/customers", request, Customer.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Customer created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getFirstname()).isEqualTo("John");
        assertThat(created.getLastname()).isEqualTo("Doe");
        assertThat(created.getEmail()).isEqualTo("john.doe@email.com");

        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(baseUrl + "/customers/" + created.getId(), Customer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Customer fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
    }

    @Test
    void deleteCustomer() {
        CustomerRequestDto request = new CustomerRequestDto("Jane", "Smith", "jane.smith@email.com", "0987654321");
        Customer created = restTemplate.postForObject(baseUrl + "/customers", request, Customer.class);
        assertThat(created).isNotNull();
        UUID id = created.getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/customers/" + id,
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    

        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(baseUrl + "/customers/" + id, Customer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCustomersWithCustomPageSize() {
        int pageSize = 2;
        String url = baseUrl + "/customers?page=0&size=" + pageSize;
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getSize()).isEqualTo(pageSize);
        assertThat(page.getData().size()).isLessThanOrEqualTo(pageSize);
    }

    @Test
    void getNonExistentCustomerReturnsNotFound() {
        String url = baseUrl + "/customers/00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteNonExistentCustomerReturnsNotFound() {
        String url = baseUrl + "/customers/00000000-0000-0000-0000-000000000000";
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createCustomerWithDuplicateEmailReturnsError() {
        CustomerRequestDto request = new CustomerRequestDto("Dup", "Email", "dup.email@example.com", "0123456789");
        restTemplate.postForEntity(baseUrl + "/customers", request, Customer.class);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/customers", request, String.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createCustomerWithMissingFieldsReturnsError() {
        CustomerRequestDto request = new CustomerRequestDto(null, null, null, null);
        ResponseEntity<ValidationErrorResponseDto> response = restTemplate.postForEntity(baseUrl + "/customers", request, ValidationErrorResponseDto.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getValidationErrors().isEmpty()).isFalse();
    }
}
