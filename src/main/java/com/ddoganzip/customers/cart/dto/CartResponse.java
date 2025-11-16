package com.ddoganzip.customers.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long itemId;
        private Long dinnerId;
        private String dinnerName;
        private BigDecimal dinnerBasePrice;
        private Long servingStyleId;
        private String servingStyleName;
        private BigDecimal servingStylePrice;
        private Integer quantity;
        private BigDecimal itemTotalPrice;
        private List<CustomizationResponse> customizations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationResponse {
        private String action;
        private Long dishId;
        private String dishName;
        private Integer quantity;
    }
}
