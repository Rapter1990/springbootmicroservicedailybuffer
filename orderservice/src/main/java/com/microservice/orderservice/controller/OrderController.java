package com.microservice.orderservice.controller;

import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/placeorder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {

        log.info("OrderController | placeOrder is called");

        log.info("OrderController | placeOrder | orderRequest: {}", orderRequest.toString());

        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId, @RequestHeader("Authorization") String bearerToken) {

        log.info("OrderController | getOrderDetails is called");

        log.info("OrderController | getOrderDetails | Authorization : " + bearerToken);

        OrderResponse orderResponse
                = orderService.getOrderDetails(orderId, bearerToken);

        log.info("OrderController | getOrderDetails | orderResponse : " + orderResponse.toString());

        return new ResponseEntity<>(orderResponse,
                HttpStatus.OK);
    }
}
