package com.microservice.orderservice.service;

import com.microservice.orderservice.exception.CustomException;
import com.microservice.orderservice.external.client.PaymentService;
import com.microservice.orderservice.external.client.ProductService;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.request.PaymentRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.payload.response.PaymentResponse;
import com.microservice.orderservice.payload.response.ProductResponse;
import com.microservice.orderservice.repository.OrderRepository;
import com.microservice.orderservice.service.impl.OrderServiceImpl;
import com.microservice.orderservice.utils.PaymentMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    private OrderRepository orderRepository;

    private ProductService productService;

    private PaymentService paymentService;

    private RestTemplate restTemplate;

    OrderService orderService;

    @BeforeEach
    void setup(){
        orderRepository = mock(OrderRepository.class);
        productService = mock(ProductService.class);
        paymentService = mock(PaymentService.class);
        restTemplate = mock(RestTemplate.class);
        orderService = new OrderServiceImpl(orderRepository, productService, paymentService, restTemplate);
    }

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {

        String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJVc2VyIiwiaXNzIjoiUk9MRV9VU0VSICIsImlhdCI6MTY3MjQ0NDI2MiwiZXhwIjoxNjcyNDQ0MzgyfQ.8QKm8VKgi8zHAo7YGngB00ng6XrByyofzUkEq_3g4omRA_ODGpwjDYSDNIvpEKXEATt6oMWV9JrMkHz3hI-xOw";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ bearerToken);

        HttpEntity request = new HttpEntity<>(headers);

        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));


        when(restTemplate.exchange(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                HttpMethod.GET, request, ProductResponse.class)).thenReturn(ResponseEntity.ok(getMockProductResponse()));

        when(restTemplate.exchange(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                HttpMethod.GET, request, PaymentResponse.class)).thenReturn(ResponseEntity.ok(getMockPaymentResponse()));

        //Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1,"Bearer "+ bearerToken);

        //Verification
        verify(orderRepository, times(1)).findById(anyLong());

        verify(restTemplate, times(1))
                .exchange("http://PRODUCT-SERVICE/product/" + order.getProductId(), HttpMethod.GET,
                request, ProductResponse.class);

        verify(restTemplate, times(1))
                .exchange("http://PAYMENT-SERVICE/payment/order/" + order.getId(), HttpMethod.GET,
                request, PaymentResponse.class);

        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJVc2VyIiwiaXNzIjoiUk9MRV9VU0VSICIsImlhdCI6MTY3MTM5NTQ0MCwiZXhwIjoxNjcxMzk1NTYwfQ.fBhI_flxuuXZfwhd8hEVdfkZkMNobVsi4hAdSXdl5qqWRedJWQWZXwYVdfSof6ezH7myZNQgn-kRNBXIDDHGDQ";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(1,bearerToken));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        verify(orderRepository, times(1))
                .findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {

        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_when_Place_Order_Payment_Fails_then_Order_Placed() {

        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(1)
                .productName("Product 1")
                .price(0)
                .quantity(0)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("CREATED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .quantity(0)
                .productId(1)
                .build();
    }
}
