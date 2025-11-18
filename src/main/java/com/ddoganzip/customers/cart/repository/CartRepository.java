package com.ddoganzip.customers.cart.repository;

import com.ddoganzip.customers.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.dinner LEFT JOIN FETCH ci.servingStyle WHERE c.customer.id = :customerId")
    Optional<Cart> findByCustomerIdWithItems(Long customerId);

    Optional<Cart> findByCustomerId(Long customerId);
}
