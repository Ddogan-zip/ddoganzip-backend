package com.ddoganzip.customers.cart.entity;

import com.ddoganzip.customers.menu.entity.Dinner;
import com.ddoganzip.customers.menu.entity.ServingStyle;
import com.ddoganzip.customers.orders.entity.CustomizationAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "dinner_id")
    private Dinner dinner;

    @ManyToOne
    @JoinColumn(name = "serving_style_id")
    private ServingStyle servingStyle;

    @Column(nullable = false)
    private Integer quantity = 1;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizationAction> customizations = new ArrayList<>();
}
