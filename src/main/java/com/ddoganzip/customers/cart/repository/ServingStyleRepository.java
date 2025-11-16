package com.ddoganzip.customers.cart.repository;

import com.ddoganzip.entity.ServingStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServingStyleRepository extends JpaRepository<ServingStyle, Long> {
}
