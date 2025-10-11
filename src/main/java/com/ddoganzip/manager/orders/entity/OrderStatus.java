package com.ddoganzip.manager.orders.entity;

public enum OrderStatus {
    PENDING, // 주문대기
    PREPARING, //준비중
    DELIVERING, //배달중
    DELIVERD, //배달완료
    CANCELLED, //주문취소
}
