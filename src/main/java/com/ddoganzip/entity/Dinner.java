package com.ddoganzip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dinners")
@Getter
@Setter
@NoArgsConstructor
public class Dinner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer basePrice;

    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "dinner_dishes",
            joinColumns = @JoinColumn(name = "dinner_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "dinner_serving_styles",
            joinColumns = @JoinColumn(name = "dinner_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id")
    )
    private List<ServingStyle> availableStyles = new ArrayList<>();
}
