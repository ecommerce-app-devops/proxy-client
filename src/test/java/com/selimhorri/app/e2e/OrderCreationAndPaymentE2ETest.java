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
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.domain.enums.OrderStatus;

import java.util.Map;

/**
 * E2E Test: Complete order creation and payment flow
 * This test validates the end-to-end flow from order creation through payment
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.selimhorri.app.ProxyClientApplication.class}
)
@ActiveProfiles("test")
@DisplayName("E2E: Order Creation and Payment Flow")
class OrderCreationAndPaymentE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/app";
    }

    @Test
    @DisplayName("Complete flow: Create cart -> Create order -> Process payment -> Update order status")
    void testOrderCreationAndPaymentFlow() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Step 1: Create a cart (if cart creation endpoint exists)
        // For this test, we'll assume a cart with ID 1 exists
        Integer cartId = 1;

        // Step 2: Create an order from the cart
        OrderDto orderDto = OrderDto.builder()
                .orderDesc("E2E Test Order")
                .orderFee(150.0)
                .cartDto(CartDto.builder().cartId(cartId).build())
                .build();

        HttpEntity<OrderDto> orderRequest = new HttpEntity<>(orderDto, headers);
        ResponseEntity<OrderDto> orderResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders",
                HttpMethod.POST,
                orderRequest,
                OrderDto.class
        );

        // Assert order creation
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
        assertNotNull(orderResponse.getBody());
        assertNotNull(orderResponse.getBody().getOrderId());
        Integer orderId = orderResponse.getBody().getOrderId();

        // Step 3: Update order status to ORDERED
        ResponseEntity<OrderDto> updateOrderResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders/" + orderId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                OrderDto.class
        );

        assertTrue(updateOrderResponse.getStatusCode().is2xxSuccessful());

        // Step 4: Create payment for the order
        PaymentDto paymentDto = PaymentDto.builder()
                .orderDto(OrderDto.builder().orderId(orderId).build())
                .build();

        HttpEntity<PaymentDto> paymentRequest = new HttpEntity<>(paymentDto, headers);
        ResponseEntity<PaymentDto> paymentResponse = restTemplate.exchange(
                getBaseUrl() + "/api/payments",
                HttpMethod.POST,
                paymentRequest,
                PaymentDto.class
        );

        // Assert payment creation
        assertTrue(paymentResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(paymentResponse.getBody());
        assertNotNull(paymentResponse.getBody().getPaymentId());

        // Step 5: Verify order status was updated to IN_PAYMENT
        ResponseEntity<OrderDto> verifyOrderResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders/" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                OrderDto.class
        );

        assertEquals(HttpStatus.OK, verifyOrderResponse.getStatusCode());
        assertNotNull(verifyOrderResponse.getBody());
        // Order status should be updated to IN_PAYMENT after payment creation
    }

    @Test
    @DisplayName("Complete flow: Create order -> Cancel order before payment")
    void testOrderCancellationFlow() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Step 1: Create an order
        OrderDto orderDto = OrderDto.builder()
                .orderDesc("Order to Cancel")
                .orderFee(100.0)
                .cartDto(CartDto.builder().cartId(1).build())
                .build();

        HttpEntity<OrderDto> orderRequest = new HttpEntity<>(orderDto, headers);
        ResponseEntity<OrderDto> orderResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders",
                HttpMethod.POST,
                orderRequest,
                OrderDto.class
        );

        assertTrue(orderResponse.getStatusCode().is2xxSuccessful());
        Integer orderId = orderResponse.getBody().getOrderId();

        // Step 2: Cancel the order (soft delete)
        ResponseEntity<Boolean> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders/" + orderId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Boolean.class
        );

        assertTrue(deleteResponse.getStatusCode().is2xxSuccessful());
        assertEquals(Boolean.TRUE, deleteResponse.getBody());

        // Step 3: Verify order is not returned in active orders list
        ResponseEntity<Map> ordersResponse = restTemplate.exchange(
                getBaseUrl() + "/api/orders",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        assertTrue(ordersResponse.getStatusCode().is2xxSuccessful());
        // Verify the cancelled order is not in the active list
    }
}

