package com.ddoganzip.staff.controller;

import com.ddoganzip.common.ApiResponse;
import com.ddoganzip.staff.dto.ActiveOrdersResponse;
import com.ddoganzip.staff.dto.InventoryItemResponse;
import com.ddoganzip.staff.dto.OrderInventoryCheckResponse;
import com.ddoganzip.staff.dto.StaffAvailabilityResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import com.ddoganzip.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/orders/active")
    public ResponseEntity<List<ActiveOrdersResponse>> getActiveOrders() {
        log.info("=== [StaffController] GET /api/staff/orders/active - START ===");
        try {
            List<ActiveOrdersResponse> activeOrders = staffService.getActiveOrders();
            log.info("[StaffController] Retrieved {} active orders", activeOrders.size());
            return ResponseEntity.ok(activeOrders);
        } catch (Exception e) {
            log.error("[StaffController] Failed to get active orders: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] GET /api/staff/orders/active - END ===");
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("=== [StaffController] PUT /api/staff/orders/{}/status - START ===", orderId);
        log.info("[StaffController] Updating order {} to status: {}", orderId, request.getStatus());
        try {
            staffService.updateOrderStatus(orderId, request);
            log.info("[StaffController] Order status updated successfully");
            return ResponseEntity.ok(ApiResponse.success("Order status updated"));
        } catch (Exception e) {
            log.error("[StaffController] Failed to update order status: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] PUT /api/staff/orders/{}/status - END ===", orderId);
        }
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryItemResponse>> getInventory() {
        log.info("=== [StaffController] GET /api/staff/inventory - START ===");
        try {
            List<InventoryItemResponse> inventory = staffService.getInventory();
            log.info("[StaffController] Retrieved {} inventory items", inventory.size());
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            log.error("[StaffController] Failed to get inventory: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] GET /api/staff/inventory - END ===");
        }
    }

    @GetMapping("/orders/{orderId}/inventory-check")
    public ResponseEntity<OrderInventoryCheckResponse> checkOrderInventory(@PathVariable Long orderId) {
        log.info("=== [StaffController] GET /api/staff/orders/{}/inventory-check - START ===", orderId);
        try {
            OrderInventoryCheckResponse result = staffService.checkOrderInventory(orderId);
            log.info("[StaffController] Inventory check completed - isSufficient: {}, items: {}",
                result.getIsSufficient(), result.getRequiredItems().size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[StaffController] Failed to check order inventory: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] GET /api/staff/orders/{}/inventory-check - END ===", orderId);
        }
    }

    @GetMapping("/availability")
    public ResponseEntity<StaffAvailabilityResponse> getStaffAvailability() {
        log.info("=== [StaffController] GET /api/staff/availability - START ===");
        try {
            StaffAvailabilityResponse availability = staffService.getStaffAvailability();
            log.info("[StaffController] Staff availability - cooks: {}/{}, drivers: {}/{}",
                availability.getAvailableCooks(), availability.getTotalCooks(),
                availability.getAvailableDrivers(), availability.getTotalDrivers());
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            log.error("[StaffController] Failed to get staff availability: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] GET /api/staff/availability - END ===");
        }
    }

    @PostMapping("/drivers/return")
    public ResponseEntity<ApiResponse<Void>> driverReturn() {
        log.info("=== [StaffController] POST /api/staff/drivers/return - START ===");
        try {
            staffService.driverReturn();
            log.info("[StaffController] Driver returned successfully");
            return ResponseEntity.ok(ApiResponse.success("Driver returned"));
        } catch (Exception e) {
            log.error("[StaffController] Failed to return driver: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [StaffController] POST /api/staff/drivers/return - END ===");
        }
    }
}
