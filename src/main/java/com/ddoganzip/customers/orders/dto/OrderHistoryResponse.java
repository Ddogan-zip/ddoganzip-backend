package com.ddoganzip.customers.orders.dto;

import com.ddoganzip.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponse {
    private Long orderId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String deliveryAddress;
    private OrderStatus status;
    private Integer totalPrice;
    private int itemCount;
}
