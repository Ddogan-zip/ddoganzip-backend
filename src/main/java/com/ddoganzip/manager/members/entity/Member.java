package com.ddoganzip.manager.members.entity;


import com.ddoganzip.customers.orders.entity.MemberLevel;
import com.ddoganzip.manager.orders.entity.Order;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private UUID id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String address;

    private String distance;

    @Enumerated(EnumType.STRING)
    private MemberLevel level;

    @OneToMany(mappedBy = "id")
    List<Order> orders = new ArrayList<Order>();


}
