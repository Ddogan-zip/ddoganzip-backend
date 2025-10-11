package com.ddoganzip.manager.orders.entity;

import com.ddoganzip.manager.orders.entity.OrderDetail;
import com.ddoganzip.manager.members.entity.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DinnerType dinnerType;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServingStyle servingStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus order_status;

    @Column(nullable = false)
    private BigDecimal total_price;

    @CreationTimestamp // 엔티티 생성 시 현재 시간을 자동으로 저장
    private LocalDateTime ordered_at;

    @OneToMany(mappedBy = "id")
    private List<OrderDetail> orderDetails = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // DB의 orders 테이블에 생성될 외래 키(FK) 컬럼명
    private Member member;

}
