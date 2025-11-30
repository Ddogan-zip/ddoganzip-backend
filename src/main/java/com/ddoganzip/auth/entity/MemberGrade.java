package com.ddoganzip.auth.entity;

public enum MemberGrade {
    NORMAL(0, 0),      // 일반: 0% 할인, 0회 주문
    BRONZE(5, 5),      // 브론즈: 5% 할인, 5회 주문
    SILVER(8, 10),     // 실버: 8% 할인, 10회 주문
    GOLD(11, 15),      // 골드: 11% 할인, 15회 주문
    VIP(15, 20);       // VIP: 15% 할인, 20회 주문

    private final int discountPercent;
    private final int requiredOrders;

    MemberGrade(int discountPercent, int requiredOrders) {
        this.discountPercent = discountPercent;
        this.requiredOrders = requiredOrders;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public int getRequiredOrders() {
        return requiredOrders;
    }

    /**
     * 주문 횟수에 따른 등급 계산
     */
    public static MemberGrade calculateGrade(int orderCount) {
        if (orderCount >= VIP.requiredOrders) {
            return VIP;
        } else if (orderCount >= GOLD.requiredOrders) {
            return GOLD;
        } else if (orderCount >= SILVER.requiredOrders) {
            return SILVER;
        } else if (orderCount >= BRONZE.requiredOrders) {
            return BRONZE;
        } else {
            return NORMAL;
        }
    }

    /**
     * 할인 금액 계산
     */
    public int calculateDiscount(int originalPrice) {
        return (originalPrice * discountPercent) / 100;
    }
}
