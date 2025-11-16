package com.ddoganzip.customers.orders.controller;

import com.ddoganzip.common.ApiResponse;
import com.ddoganzip.customers.orders.dto.*;
import com.ddoganzip.customers.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Long>> checkout(@Valid @RequestBody CheckoutRequest request) {
        Long orderId = orderService.checkout(request);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", orderId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory() {
        List<OrderHistoryResponse> history = orderService.getOrderHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetails(@PathVariable Long orderId) {
        OrderDetailResponse details = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(details);
    }
}
