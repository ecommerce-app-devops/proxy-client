package com.selimhorri.app.e2e;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.UserDto;

/**
 * E2E Test: User profile management flow
 * This test validates the complete flow of user profile updates
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.selimhorri.app.ProxyClientApplication.class}
)
@ActiveProfiles("test")
@DisplayName("E2E: User Profile Management Flow")
class UserProfileManagementE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/app";
    }

    @Test
    @DisplayName("Complete flow: Create user -> Update profile -> Verify changes")
    void testUserProfileUpdateFlow() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Step 1: Create a user
        UserDto newUser = UserDto.builder()
                .firstName("Original")
                .lastName("Name")
                .email("original@example.com")
                .phone("1234567890")
                .build();

        HttpEntity<UserDto> userRequest = new HttpEntity<>(newUser, headers);
        ResponseEntity<UserDto> createResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                userRequest,
                UserDto.class
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Integer userId = createResponse.getBody().getUserId();

        // Step 2: Update user profile
        UserDto updatedUser = UserDto.builder()
                .userId(userId)
                .firstName("Updated")
                .lastName("Profile")
                .email("updated@example.com")
                .phone("9876543210")
                .build();

        HttpEntity<UserDto> updateRequest = new HttpEntity<>(updatedUser, headers);
        ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.PUT,
                updateRequest,
                UserDto.class
        );

        // Assert update was successful
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated", updateResponse.getBody().getFirstName());
        assertEquals("Profile", updateResponse.getBody().getLastName());
        assertEquals("updated@example.com", updateResponse.getBody().getEmail());

        // Step 3: Verify changes by retrieving user
        ResponseEntity<UserDto> getResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Updated", getResponse.getBody().getFirstName());
        assertEquals("Profile", getResponse.getBody().getLastName());
    }

    @Test
    @DisplayName("Complete flow: Create user -> Update by ID -> Verify changes")
    void testUserProfileUpdateByIdFlow() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Step 1: Create a user
        UserDto newUser = UserDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .phone("1111111111")
                .build();

        HttpEntity<UserDto> userRequest = new HttpEntity<>(newUser, headers);
        ResponseEntity<UserDto> createResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                userRequest,
                UserDto.class
        );

        Integer userId = createResponse.getBody().getUserId();

        // Step 2: Update user by ID
        UserDto updatedUser = UserDto.builder()
                .firstName("Modified")
                .lastName("User")
                .email("modified@example.com")
                .phone("2222222222")
                .build();

        HttpEntity<UserDto> updateRequest = new HttpEntity<>(updatedUser, headers);
        ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.PUT,
                updateRequest,
                UserDto.class
        );

        // Assert update was successful
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Modified", updateResponse.getBody().getFirstName());
    }
}

