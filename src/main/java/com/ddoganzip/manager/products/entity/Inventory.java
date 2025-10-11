package com.ddoganzip.manager.products.entity;


import com.ddoganzip.manager.orders.entity.ItemCode;
import com.ddoganzip.manager.orders.entity.OrderDetail;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ItemCode itemCode;

    private String itemName;

    @OneToMany(mappedBy = "id")
    private List<MenuItem> menuItems = new ArrayList<>();

    private int quantity;
}
