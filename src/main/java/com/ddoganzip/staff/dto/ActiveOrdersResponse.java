package com.ddoganzip.staff.dto;

import com.ddoganzip.customers.orders.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveOrdersResponse {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String deliveryAddress;
    private OrderStatus status;
    private Integer totalPrice;
    private int itemCount;
}
