package com.ddoganzip.customers.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuListResponse {
    private Long id;
    private String name;
    private String description;
    private Integer basePrice;
    private String imageUrl;
}
