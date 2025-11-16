package com.ddoganzip.customers.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOptionsRequest {
    @NotNull(message = "Serving style ID is required")
    private Long servingStyleId;

    private List<AddToCartRequest.CustomizationRequest> customizations;
}
