package com.ddoganzip.customers.menu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer basePrice;

    private Integer defaultQuantity;

    @Column(name = "current_stock")
    private Integer currentStock = 0;

    @Column(name = "minimum_stock")
    private Integer minimumStock = 10;
}
