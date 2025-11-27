package com.ddoganzip.customers.orders.entity;

public enum OrderStatus {
    CHECKING_STOCK,
    RECEIVED,
    IN_KITCHEN,
    COOKED,
    DELIVERING,
    DELIVERED,
    DRIVER_RETURNED  // 배달 완료 후 기사 복귀 완료
}
