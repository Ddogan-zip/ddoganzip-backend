package com.ddoganzip.customers.orders.entity;

public enum MemberLevel {
    BRONZE(5),
    SILVER(10),
    GOLD(15),
    MANAGER(20);

    private final int discount;
    MemberLevel(int i) {
        this.discount = i;
    }

    public int getDiscount() {
        return discount;
    }


    //만약 MemberLevel.BRONZE로 Enum 객체를 생성했다면
    //컴파일러에서 자동으로 생성자를 실행하여 BRONZE에 저장된 5이라는 값을
    //private final int value 에 저장한다.
    //따라서, 이 Enum 객체의 getDiscount() 메소드를 실행하면 5이라는 값이 리턴되는 것이다.
}
