package com.ddoganzip.customers.orders.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private LocalDateTime deliveryDate;
}
