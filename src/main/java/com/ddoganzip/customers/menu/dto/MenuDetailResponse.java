package com.ddoganzip.customers.menu.dto;

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
public class MenuDetailResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private List<DishInfo> dishes;
    private List<ServingStyleInfo> availableStyles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishInfo {
        private Long id;
        private String name;
        private Integer defaultQuantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServingStyleInfo {
        private Long id;
        private String name;
        private BigDecimal additionalPrice;
        private String description;
    }
}
