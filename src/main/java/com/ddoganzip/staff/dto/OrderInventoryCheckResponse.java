package com.ddoganzip.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInventoryCheckResponse {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private Boolean isSufficient;  // 전체 재고가 충분한지
    private List<DishRequirement> requiredItems;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishRequirement {
        private Long dishId;
        private String dishName;
        private Integer requiredQuantity;  // 이 주문에 필요한 수량
        private Integer currentStock;      // 현재 재고
        private Boolean isSufficient;      // 이 항목이 충분한지
        private Integer shortage;          // 부족한 수량 (음수면 충분, 양수면 부족)
    }
}
