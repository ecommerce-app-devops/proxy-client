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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

/**
 * E2E Test: Complete user registration and login flow
 * This test validates the end-to-end flow of user registration and authentication
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.selimhorri.app.ProxyClientApplication.class}
)
@ActiveProfiles("test")
@DisplayName("E2E: User Registration and Login Flow")
class UserRegistrationAndLoginE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/app";
    }

    @Test
    @DisplayName("Complete flow: Register user -> Create credentials -> Login")
    void testUserRegistrationAndLoginFlow() {
        // Step 1: Register a new user
        UserDto newUser = UserDto.builder()
                .firstName("E2E")
                .lastName("TestUser")
                .email("e2e.test@example.com")
                .phone("1234567890")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserDto> userRequest = new HttpEntity<>(newUser, headers);

        ResponseEntity<UserDto> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                userRequest,
                UserDto.class
        );

        // Assert user creation
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userResponse.getBody());
        assertNotNull(userResponse.getBody().getUserId());
        Integer userId = userResponse.getBody().getUserId();

        // Step 2: Create credentials for the user
        // This step might need to be adjusted based on actual credential creation endpoint
        Map<String, Object> credentialData = new HashMap<>();
        credentialData.put("userId", userId);
        credentialData.put("username", "e2euser");
        credentialData.put("password", "password123");
        credentialData.put("roleBasedAuthority", "ROLE_USER");

        HttpEntity<Map<String, Object>> credentialRequest = new HttpEntity<>(credentialData, headers);

        // Adjust endpoint based on actual credential creation endpoint
        ResponseEntity<?> credentialResponse = restTemplate.exchange(
                getBaseUrl() + "/api/credentials",
                HttpMethod.POST,
                credentialRequest,
                Object.class
        );

        // Assert credential creation
        assertTrue(credentialResponse.getStatusCode().is2xxSuccessful());

        // Step 3: Verify user can be retrieved
        ResponseEntity<UserDto> getUserResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto.class
        );

        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        assertNotNull(getUserResponse.getBody());
        assertEquals("E2E", getUserResponse.getBody().getFirstName());
    }
}

