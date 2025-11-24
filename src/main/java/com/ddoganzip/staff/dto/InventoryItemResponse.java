package com.ddoganzip.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemResponse {
    private Long dishId;
    private String dishName;
    private Integer currentStock;
    private Integer minimumStock;
}
