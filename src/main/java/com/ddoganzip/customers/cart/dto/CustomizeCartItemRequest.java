package com.ddoganzip.customers.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomizeCartItemRequest {
    @NotNull
    private String action;  // "ADD", "REMOVE", "REPLACE"

    @NotNull
    private Long dishId;

    @NotNull
    private Integer quantity;  // 1 이상 (ADD/REPLACE), REMOVE는 무시됨
}
