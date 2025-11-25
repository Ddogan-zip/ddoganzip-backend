package com.ddoganzip.customers.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private Integer totalPrice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long itemId;
        private Long dinnerId;
        private String dinnerName;
        private Integer dinnerBasePrice;
        private Long servingStyleId;
        private String servingStyleName;
        private Integer servingStylePrice;
        private Integer quantity;
        private Integer itemTotalPrice;
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
        private Integer pricePerUnit;  // 단가 (요리 1개당 가격)
    }
}
