package com.ddoganzip.staff.service;

import com.ddoganzip.customers.cart.repository.DishRepository;
import com.ddoganzip.customers.menu.entity.Dish;
import com.ddoganzip.customers.orders.repository.OrderRepository;
import com.ddoganzip.customers.orders.entity.Order;
import com.ddoganzip.customers.orders.entity.OrderStatus;
import com.ddoganzip.exception.CustomException;
import com.ddoganzip.staff.dto.ActiveOrdersResponse;
import com.ddoganzip.staff.dto.InventoryItemResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    @Transactional(readOnly = true)
    public List<ActiveOrdersResponse> getActiveOrders() {
        log.info("[StaffService] getActiveOrders() - START");
        List<Order> activeOrders = orderRepository.findActiveOrders(
                Arrays.asList(OrderStatus.DELIVERED)
        );
        log.info("[StaffService] Found {} active orders", activeOrders.size());

        List<ActiveOrdersResponse> response = activeOrders.stream()
                .map(order -> ActiveOrdersResponse.builder()
                        .orderId(order.getId())
                        .customerName(order.getCustomer().getName())
                        .customerEmail(order.getCustomer().getEmail())
                        .orderDate(order.getOrderDate())
                        .deliveryDate(order.getDeliveryDate())
                        .deliveryAddress(order.getDeliveryAddress())
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .itemCount(order.getItems().size())
                        .build())
                .collect(Collectors.toList());

        log.info("[StaffService] getActiveOrders() - END");
        return response;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("[StaffService] updateOrderStatus() - START for orderId: {}, newStatus: {}",
            orderId, request.getStatus());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[StaffService] Order not found with id: {}", orderId);
                    return new CustomException("Order not found");
                });

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        log.info("[StaffService] Order {} status updated from {} to {}",
            orderId, oldStatus, request.getStatus());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventory() {
        log.info("[StaffService] getInventory() - START");
        List<Dish> dishes = dishRepository.findAll();
        log.info("[StaffService] Found {} dishes in inventory", dishes.size());

        List<InventoryItemResponse> inventory = dishes.stream()
                .map(dish -> {
                    boolean isLowStock = dish.getCurrentStock() != null &&
                                       dish.getMinimumStock() != null &&
                                       dish.getCurrentStock() < dish.getMinimumStock();
                    if (isLowStock) {
                        log.warn("[StaffService] LOW STOCK WARNING - Dish: {}, Current: {}, Minimum: {}",
                            dish.getName(), dish.getCurrentStock(), dish.getMinimumStock());
                    }
                    return InventoryItemResponse.builder()
                            .dishId(dish.getId())
                            .dishName(dish.getName())
                            .currentStock(dish.getCurrentStock() != null ? dish.getCurrentStock() : 0)
                            .minimumStock(dish.getMinimumStock() != null ? dish.getMinimumStock() : 10)
                            .build();
                })
                .collect(Collectors.toList());

        log.info("[StaffService] getInventory() - END, returning {} items", inventory.size());
        return inventory;
    }
}
