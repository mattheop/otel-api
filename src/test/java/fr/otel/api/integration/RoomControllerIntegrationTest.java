package fr.otel.api.integration;

import fr.otel.api.core.dto.PageResponseDto;
import fr.otel.api.core.dto.ValidationErrorResponseDto;
import fr.otel.api.rooms.api.dtos.RoomRequestDto;
import fr.otel.api.rooms.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import fr.otel.api.customers.api.CustomerRequestDto;
import fr.otel.api.customers.domain.Customer;
import fr.otel.api.reservations.api.dtos.ReservationRequestDto;
import fr.otel.api.reservations.api.dtos.ReservationResponseDto;

class RoomControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createAndGetRoom() {
        RoomRequestDto request = new RoomRequestDto("101A", "Deluxe", new BigDecimal("120.00"));
        ResponseEntity<Room> createResponse = restTemplate.postForEntity(baseUrl + "/rooms", request, Room.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getRoomNumber()).isEqualTo("101A");
        assertThat(created.getRoomType()).isEqualTo("Deluxe");
        assertThat(created.getPrice()).isEqualByComparingTo(new BigDecimal("120.00"));

        ResponseEntity<Room> getResponse = restTemplate.getForEntity(baseUrl + "/rooms/" + created.getId(), Room.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
    }

    @Test
    void deleteRoom() {
        RoomRequestDto request = new RoomRequestDto("102B", "Standard", new BigDecimal("80.00"));
        Room created = restTemplate.postForObject(baseUrl + "/rooms", request, Room.class);
        assertThat(created).isNotNull();
        UUID id = created.getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/rooms/" + id,
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Room> getResponse = restTemplate.getForEntity(baseUrl + "/rooms/" + id, Room.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getRoomsWithCustomPageSize() {
        int pageSize = 2;
        String url = baseUrl + "/rooms?page=0&size=" + pageSize;
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getSize()).isEqualTo(pageSize);
        assertThat(page.getData().size()).isLessThanOrEqualTo(pageSize);
    }

    @Test
    void getNonExistentRoomReturnsNotFound() {
        String url = baseUrl + "/rooms/00000000-0000-0000-0000-000000000000";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteNonExistentRoomReturnsNotFound() {
        String url = baseUrl + "/rooms/00000000-0000-0000-0000-000000000000";
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createRoomWithDuplicateRoomNumberReturnsError() {
        RoomRequestDto request = new RoomRequestDto("103C", "Suite", new BigDecimal("200.00"));
        restTemplate.postForEntity(baseUrl + "/rooms", request, Room.class);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/rooms", request, String.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createRoomWithMissingFieldsReturnsError() {
        RoomRequestDto request = new RoomRequestDto(null, null, null);
        ResponseEntity<ValidationErrorResponseDto> response = restTemplate.postForEntity(baseUrl + "/rooms", request, ValidationErrorResponseDto.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getValidationErrors().isEmpty()).isFalse();
    }

    @Test
    void getAvailableRoomsWithOverlappingReservationReturnsEmpty() {
        RoomRequestDto roomRequest = new RoomRequestDto("201A", "Deluxe", new BigDecimal("150.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        assertThat(room).isNotNull();

        CustomerRequestDto customerRequest = new CustomerRequestDto("Test", "User", "test.user@email.com", "0123456789");
        Customer customer = restTemplate.postForObject(baseUrl + "/customers", customerRequest, Customer.class);
        assertThat(customer).isNotNull();

        ReservationRequestDto reservationRequest = new ReservationRequestDto(
            customer.getId(),
            room.getId(),
            LocalDate.of(2026, 6, 10),
            LocalDate.of(2026, 6, 12),
            "Test reservation"
        );
        ReservationResponseDto reservation = restTemplate.postForObject(baseUrl + "/reservations", reservationRequest, ReservationResponseDto.class);
        assertThat(reservation).isNotNull();

        String url = baseUrl + "/rooms?isAvailable=1&from=2026-06-11&to=2026-06-11";
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getData()).isEmpty();
    }

    @Test
    void getAvailableRoomsWithNoOverlappingReservationReturnsRoom() {
        RoomRequestDto roomRequest = new RoomRequestDto("202B", "Standard", new BigDecimal("100.00"));
        Room room = restTemplate.postForObject(baseUrl + "/rooms", roomRequest, Room.class);
        assertThat(room).isNotNull();

        String url = baseUrl + "/rooms?isAvailable=1&from=2026-06-11&to=2026-06-11";
        ResponseEntity<PageResponseDto> response = restTemplate.getForEntity(url, PageResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PageResponseDto page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getData()).isNotEmpty();
        boolean found = page.getData().stream().anyMatch(r -> ((java.util.LinkedHashMap<?, ?>) r).get("id").equals(room.getId().toString()));
        assertThat(found).isTrue();
    }
} 