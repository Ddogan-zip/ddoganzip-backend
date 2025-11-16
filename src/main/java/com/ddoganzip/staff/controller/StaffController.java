package com.ddoganzip.staff.controller;

import com.ddoganzip.common.ApiResponse;
import com.ddoganzip.staff.dto.ActiveOrdersResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import com.ddoganzip.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/orders/active")
    public ResponseEntity<List<ActiveOrdersResponse>> getActiveOrders() {
        List<ActiveOrdersResponse> activeOrders = staffService.getActiveOrders();
        return ResponseEntity.ok(activeOrders);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        staffService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated"));
    }
}
