package com.ddoganzip.customers.orders.entity;

import com.ddoganzip.customers.cart.entity.CartItem;
import com.ddoganzip.customers.menu.entity.Dish;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customization_actions")
@Getter
@Setter
@NoArgsConstructor
public class CustomizationAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_item_id")
    private CartItem cartItem;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private String action; // ADD, REMOVE, REPLACE

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    private Integer quantity;
}
