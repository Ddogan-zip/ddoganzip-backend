package com.ddoganzip.staff.service;

import com.ddoganzip.customers.orders.repository.OrderRepository;
import com.ddoganzip.entity.Order;
import com.ddoganzip.entity.OrderStatus;
import com.ddoganzip.exception.CustomException;
import com.ddoganzip.staff.dto.ActiveOrdersResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<ActiveOrdersResponse> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findActiveOrders(
                Arrays.asList(OrderStatus.DELIVERED)
        );

        return activeOrders.stream()
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
    }

    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        order.setStatus(request.getStatus());
        orderRepository.save(order);
    }
}
