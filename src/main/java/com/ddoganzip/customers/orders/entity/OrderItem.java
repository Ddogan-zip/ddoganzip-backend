package com.ddoganzip.customers.orders.entity;

import com.ddoganzip.customers.menu.entity.Dinner;
import com.ddoganzip.customers.menu.entity.ServingStyle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "dinner_id")
    private Dinner dinner;

    @ManyToOne
    @JoinColumn(name = "serving_style_id")
    private ServingStyle servingStyle;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<CustomizationAction> customizations = new ArrayList<>();
}
