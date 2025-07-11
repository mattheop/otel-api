package fr.otel.api.integration;

import fr.otel.api.core.dto.PageResponseDto;
import fr.otel.api.core.dto.ValidationErrorResponseDto;
import fr.otel.api.customers.api.CustomerRequestDto;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.reservations.api.ReservationRequestDto;
import fr.otel.api.reservations.api.ReservationResponseDto;
import fr.otel.api.rooms.api.RoomRequestDto;
import fr.otel.api.rooms.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createAndGetReservation() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("John", "Doe", "john.doe@email.com", "0123456789");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        assertThat(customer).isNotNull();

        RoomRequestDto roomRequest = new RoomRequestDto("101A", "Deluxe", new BigDecimal("120.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        assertThat(room).isNotNull();

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(2);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate,
            endDate,
            "Test reservation"
        );

        ResponseEntity<ReservationResponseDto> createResponse = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            ReservationResponseDto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationResponseDto created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(created.getRoom().getId()).isEqualTo(room.getId());
        assertThat(created.getStartDate()).isEqualTo(startDate);
        assertThat(created.getEndDate()).isEqualTo(endDate);
        assertThat(created.getNote()).isEqualTo("Test reservation");

        ResponseEntity<ReservationResponseDto> getResponse = restTemplate.getForEntity(
            baseUrl + "/reservations/" + created.getId(), 
            ReservationResponseDto.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationResponseDto fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(fetched.getRoom().getId()).isEqualTo(room.getId());
    }

    @Test
    void getReservationsWithPagination() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("Jane", "Smith", "jane.smith@email.com", "0987654321");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("102B", "Standard", new BigDecimal("80.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);

        for (int i = 0; i < 3; i++) {
            LocalDate startDate = LocalDate.now().plusDays(i + 1);
            LocalDate endDate = startDate.plusDays(1);
            
            ReservationRequestDto request = new ReservationRequestDto(
                customer.getId(),
                room.getId(),
                startDate,
                endDate,
                "Test reservation " + i
            );
            restTemplate.postForObject(baseUrl + "/reservations", request, ReservationResponseDto.class);
        }

        int pageSize = 2;
        String url = baseUrl + "/reservations?page=0&size=" + pageSize;
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto<ReservationResponseDto> reservations = response.getBody();
        assertThat(reservations).isNotNull();
        assertThat(reservations.getSize()).isLessThanOrEqualTo(pageSize);
    }

    @Test
    void getNonExistentReservationReturnsNotFound() {
        String url = baseUrl + "/reservations/00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createReservationWithNonExistentCustomerReturnsError() {
        // Create room but not customer
        RoomRequestDto roomRequest = new RoomRequestDto("103C", "Suite", new BigDecimal("200.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(2);
        
        ReservationRequestDto request = new ReservationRequestDto(
            UUID.randomUUID(), // Non-existent customer
            room.getId(),
            startDate,
            endDate,
            "Test reservation"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            String.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithNonExistentRoomReturnsError() {
        // Create customer but not room
        CustomerRequestDto customerRequest = new CustomerRequestDto("Bob", "Wilson", "bob.wilson@email.com", "0555666777");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(2);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            UUID.randomUUID(), // Non-existent room
            startDate,
            endDate,
            "Test reservation"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            String.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithInvalidDateRangeReturnsError() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("Alice", "Brown", "alice.brown@email.com", "0444555666");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("104D", "Premium", new BigDecimal("150.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        
        // End date before start date
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = startDate.minusDays(1);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate,
            endDate,
            "Invalid date range"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            String.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithPastStartDateReturnsError() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("Charlie", "Davis", "charlie.davis@email.com", "0333444555");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("105E", "Economy", new BigDecimal("60.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        
        // Past start date
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = startDate.plusDays(2);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate,
            endDate,
            "Past start date"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            String.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithMissingFieldsReturnsError() {
        ReservationRequestDto request = new ReservationRequestDto(null, null, null, null, null);
        ResponseEntity<ValidationErrorResponseDto> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            ValidationErrorResponseDto.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getValidationErrors().isEmpty()).isFalse();
    }

    @Test
    void createConflictingReservationsReturnsError() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("David", "Miller", "david.miller@email.com", "0222333444");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("106F", "Deluxe", new BigDecimal("180.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        
        // Create first reservation
        LocalDate startDate1 = LocalDate.now().plusDays(1);
        LocalDate endDate1 = startDate1.plusDays(3);
        
        ReservationRequestDto request1 = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate1,
            endDate1,
            "First reservation"
        );
        
        ResponseEntity<ReservationResponseDto> response1 = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request1, 
            ReservationResponseDto.class
        );
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Create conflicting reservation (overlapping dates)
        LocalDate startDate2 = startDate1.plusDays(1); // Overlaps with first reservation
        LocalDate endDate2 = startDate2.plusDays(2);
        
        ReservationRequestDto request2 = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate2,
            endDate2,
            "Conflicting reservation"
        );
        
        ResponseEntity<String> response2 = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request2, 
            String.class
        );
        assertThat(response2.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithSameStartAndEndDateReturnsError() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("Eva", "Garcia", "eva.garcia@email.com", "0111222333");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("107G", "Suite", new BigDecimal("250.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        
        // Same start and end date
        LocalDate sameDate = LocalDate.now().plusDays(1);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            sameDate,
            sameDate,
            "Same start and end date"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            String.class
        );
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createReservationWithVeryLongNote() {
        CustomerRequestDto customerRequest = new CustomerRequestDto("Frank", "Johnson", "frank.johnson@email.com", "0601121212");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        
        RoomRequestDto roomRequest = new RoomRequestDto("108H", "Standard", new BigDecimal("90.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        
        String longNote = "A".repeat(1000);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(2);
        
        ReservationRequestDto request = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            startDate,
            endDate,
            longNote
        );

        ResponseEntity<ReservationResponseDto> response = restTemplate.postForEntity(
            baseUrl + "/reservations", 
            request, 
            ReservationResponseDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationResponseDto created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getNote()).isEqualTo(longNote);
    }

    @Test
    void getReservationsWithSorting() {
        // Test sorting by different fields
        String url = baseUrl + "/reservations?page=0&size=5&sortBy=startDate";
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto<ReservationResponseDto> reservations = response.getBody();
        assertThat(reservations).isNotNull();
    }
} 