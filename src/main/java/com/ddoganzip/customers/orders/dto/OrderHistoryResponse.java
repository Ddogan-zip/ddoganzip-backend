package com.ddoganzip.customers.orders.dto;

import com.ddoganzip.auth.entity.MemberGrade;
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
public class OrderHistoryResponse {
    private Long orderId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime deliveredAt;
    private String deliveryAddress;
    private OrderStatus status;
    private Integer originalPrice;
    private MemberGrade appliedGrade;
    private Integer discountPercent;
    private Integer discountAmount;
    private Integer totalPrice;
    private int itemCount;
}
