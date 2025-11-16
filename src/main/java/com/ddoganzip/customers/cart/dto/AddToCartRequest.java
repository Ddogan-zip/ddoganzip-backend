package com.ddoganzip.customers.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddToCartRequest {
    @NotNull(message = "Dinner ID is required")
    private Long dinnerId;

    @NotNull(message = "Serving style ID is required")
    private Long servingStyleId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    private List<CustomizationRequest> customizations;

    @Data
    public static class CustomizationRequest {
        private String action; // ADD, REMOVE, REPLACE
        private Long dishId;
        private Integer quantity;
    }
}
