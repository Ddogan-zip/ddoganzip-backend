package com.ddoganzip.customers.orders.dto;

import com.ddoganzip.customers.orders.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String deliveryAddress;
    private OrderStatus status;
    private Integer totalPrice;
    private List<OrderItemInfo> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private Long itemId;
        private Long dinnerId;  // ✅ 추가
        private String dinnerName;
        private String servingStyleName;
        private Integer quantity;
        private Integer price;
        private List<BaseDishInfo> baseDishes;  // ✅ 추가
        private List<CustomizationInfo> customizations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseDishInfo {
        private Long dishId;
        private String dishName;
        private Integer quantity;  // Dinner에 포함된 기본 수량
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationInfo {
        private String action;
        private Long dishId;  // ✅ 추가
        private String dishName;
        private Integer quantity;
        private Integer pricePerUnit;  // ✅ 추가
    }
}
