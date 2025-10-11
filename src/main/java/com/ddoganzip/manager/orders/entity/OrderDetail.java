package com.ddoganzip.manager.orders.entity;


import com.ddoganzip.manager.products.entity.Inventory;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_detail_id")
    private UUID id;


    @Enumerated(EnumType.STRING)
    private ItemCode itemCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

}
