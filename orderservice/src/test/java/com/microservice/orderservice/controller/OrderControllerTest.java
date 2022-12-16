package com.microservice.orderservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.microservice.orderservice.OrderServiceConfig;
import com.microservice.orderservice.jwt.JwtUtils;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.payload.JWTResponse;
import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.repository.OrderRepository;
import com.microservice.orderservice.service.OrderService;
import com.microservice.orderservice.utils.PaymentMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
@ActiveProfiles("test")
public class OrderControllerTest {

    @RegisterExtension
    static WireMockExtension wireMockserver
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))
            .build();

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        wireMockserver.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getPaymentDetails() throws IOException {
        wireMockserver.stubFor(get("/payment/order/1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(copyToString(OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetPayment.json"), defaultCharset()))));
    }

    private void doPayment() {
        wireMockserver.stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {

        wireMockserver.stubFor(get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                OrderControllerTest.class
                                        .getClassLoader()
                                        .getResourceAsStream("mock/GetProduct.json"),
                                defaultCharset()))));
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(1)
                .totalAmount(100)
                .build();
    }

    @Test
    @DisplayName("Place Order -- Success Scenario")
    @WithMockUser(username = "User", authorities = { "ROLE_USER" })
    void test_When_placeOrder_DoPayment_Success() throws Exception {

        OrderRequest orderRequest = getMockOrderRequest();
        String jwt = getJWTTokenForRoleUser();

        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeorder")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

        Optional<Order> order = orderRepository.findById(Long.valueOf(orderId));
        assertTrue(order.isPresent());

        Order o = order.get();
        assertEquals(Long.parseLong(orderId), o.getId());
        assertEquals("PLACED", o.getOrderStatus());
        assertEquals(orderRequest.getTotalAmount(), o.getAmount());
        assertEquals(orderRequest.getQuantity(), o.getQuantity());

    }

    @Test
    @DisplayName("Place Order -- Failure Scenario")
    @WithMockUser(username = "Admin", authorities = { "ROLE_ADMIN" })
    public void test_When_placeOrder_WithWrongAccess_thenThrow_403() throws Exception {


        OrderRequest orderRequest = getMockOrderRequest();
        String jwt = getJWTTokenForRoleAdmin();

        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeorder")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

    }


    @Test
    @WithMockUser(username = "User", authorities = { "ROLE_USER" })
    public void test_WhenGetOrder_Success() throws Exception {

        String jwt = getJWTTokenForRoleUser();

        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.get("/order/1")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Order order = orderRepository.findById(1l).get();
        String expectedResponse = getOrderResponse(order);

        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    @WithMockUser(username = "User", authorities = { "ROLE_USER" })
    public void testWhen_GetOrder_Order_Not_Found() throws Exception {

        String jwt = getJWTTokenForRoleUser();

        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.get("/order/4")
                        .header("Authorization", "Bearer " + jwt)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    private String getOrderResponse(Order order) throws IOException {
        OrderResponse.PaymentDetails paymentDetails
                = objectMapper.readValue(
                copyToString(
                        OrderControllerTest.class.getClassLoader()
                                .getResourceAsStream("mock/GetPayment.json"
                                ),
                        defaultCharset()
                ), OrderResponse.PaymentDetails.class
        );
        paymentDetails.setPaymentStatus("SUCCESS");

        OrderResponse.ProductDetails productDetails
                = objectMapper.readValue(
                copyToString(
                        OrderControllerTest.class.getClassLoader()
                                .getResourceAsStream("mock/GetProduct.json"),
                        defaultCharset()
                ), OrderResponse.ProductDetails.class
        );

        OrderResponse orderResponse
                = OrderResponse.builder()
                .paymentDetails(paymentDetails)
                .productDetails(productDetails)
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .orderId(order.getId())
                .build();
        return objectMapper.writeValueAsString(orderResponse);
    }

    private String getJWTTokenForRoleUser() throws Exception {

        String response = wireMockserver.stubFor(get("/authenticate/login")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(copyToString(OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetJWTForUser.json"), defaultCharset())))).getResponse().getBody();


        JWTResponse jwtResponse = objectMapper.readValue(response, JWTResponse.class);

        String jwt = jwtUtils.getUserNameFromJwtToken(jwtResponse.getToken());

        return jwt;
    }

    private String getJWTTokenForRoleAdmin() throws Exception {

        String response = wireMockserver.stubFor(get("/authenticate/login")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(copyToString(OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetJWTForAdmin.json"), defaultCharset())))).getResponse().getBody();


        JWTResponse jwtResponse = objectMapper.readValue(response, JWTResponse.class);

        String jwt = jwtUtils.getUserNameFromJwtToken(jwtResponse.getToken());

        return jwt;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
