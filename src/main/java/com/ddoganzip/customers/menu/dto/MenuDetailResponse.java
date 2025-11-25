package com.ddoganzip.customers.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Integer basePrice;
    private String imageUrl;
    private List<DishInfo> dishes;
    private List<ServingStyleInfo> availableStyles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishInfo {
        private Long id;
        private String name;
        private String description;
        private Integer basePrice;
        private Integer quantity;  // Dinner에 포함된 dish의 수량
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServingStyleInfo {
        private Long id;
        private String name;
        private Integer additionalPrice;
        private String description;
    }
}
