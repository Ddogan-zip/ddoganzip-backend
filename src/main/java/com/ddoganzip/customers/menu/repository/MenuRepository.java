package com.ddoganzip.customers.menu.repository;

import com.ddoganzip.entity.Dinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Dinner, Long> {

    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.dishes LEFT JOIN FETCH d.availableStyles")
    List<Dinner> findAllWithDetails();

    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.dishes LEFT JOIN FETCH d.availableStyles WHERE d.id = :id")
    Optional<Dinner> findByIdWithDetails(Long id);
}
