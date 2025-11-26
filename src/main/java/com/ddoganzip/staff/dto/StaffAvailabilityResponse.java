package com.ddoganzip.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAvailabilityResponse {
    private Integer availableCooks;
    private Integer totalCooks;
    private Integer availableDrivers;
    private Integer totalDrivers;
    private Boolean canStartCooking;  // 조리 시작 가능 여부
    private Boolean canStartDelivery; // 배달 시작 가능 여부
}
