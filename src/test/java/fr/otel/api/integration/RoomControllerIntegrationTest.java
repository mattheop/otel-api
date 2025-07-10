package fr.otel.api.integration;

import fr.otel.api.core.dto.PageResponseDto;
import fr.otel.api.core.dto.ValidationErrorResponseDto;
import fr.otel.api.rooms.api.RoomRequestDto;
import fr.otel.api.rooms.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
} 