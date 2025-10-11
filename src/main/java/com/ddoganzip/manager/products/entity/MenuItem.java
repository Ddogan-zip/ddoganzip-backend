package com.ddoganzip.manager.products.entity;


import com.ddoganzip.manager.orders.entity.DinnerType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_item_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DinnerType dinnerType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventoryItem;

    @Column(nullable = false)
    private int defaultQuantity;

    @Column(nullable = false)
    private BigDecimal price;


}
