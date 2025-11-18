package com.ddoganzip.customers.cart.repository;

import com.ddoganzip.customers.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.customizations WHERE ci.id = :id")
    Optional<CartItem> findByIdWithCustomizations(Long id);
}
