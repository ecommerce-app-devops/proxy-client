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
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;

import java.util.List;
import java.util.Map;

/**
 * E2E Test: Browse products and add to favourites flow
 * This test validates the complete flow of browsing products and managing favourites
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.selimhorri.app.ProxyClientApplication.class}
)
@ActiveProfiles("test")
@DisplayName("E2E: Product Browse and Favourite Flow")
class ProductBrowseAndFavouriteE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/app";
    }

    @Test
    @DisplayName("Complete flow: Create user -> Browse products -> Add to favourites -> View favourites")
    void testProductBrowseAndFavouriteFlow() throws Exception {
        // Step 1: Create a user (assuming user exists or can be created)
        UserDto user = UserDto.builder()
                .firstName("Favourite")
                .lastName("User")
                .email("favourite.user@example.com")
                .phone("1234567890")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserDto> userRequest = new HttpEntity<>(user, headers);
        ResponseEntity<UserDto> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                userRequest,
                UserDto.class
        );

        assertTrue(userResponse.getStatusCode().is2xxSuccessful());
        Integer userId = userResponse.getBody().getUserId();

        // Step 2: Browse products (get all products)
        ResponseEntity<Map> productsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/products",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        assertTrue(productsResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(productsResponse.getBody());

        // Step 3: Add a product to favourites
        // Assuming we have a product with ID 1
        FavouriteDto favouriteDto = FavouriteDto.builder()
                .userId(userId)
                .productId(1)
                .build();

        HttpEntity<FavouriteDto> favouriteRequest = new HttpEntity<>(favouriteDto, headers);
        ResponseEntity<FavouriteDto> favouriteResponse = restTemplate.exchange(
                getBaseUrl() + "/api/favourites",
                HttpMethod.POST,
                favouriteRequest,
                FavouriteDto.class
        );

        // Assert favourite creation (may return 200 or 201)
        assertTrue(favouriteResponse.getStatusCode().is2xxSuccessful());

        // Step 4: Retrieve user's favourites
        ResponseEntity<Map> favouritesResponse = restTemplate.exchange(
                getBaseUrl() + "/api/favourites",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        assertTrue(favouritesResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(favouritesResponse.getBody());
    }
}

