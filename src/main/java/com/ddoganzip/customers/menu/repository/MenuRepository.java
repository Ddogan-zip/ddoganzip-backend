package com.ddoganzip.customers.menu.repository;

import com.ddoganzip.customers.menu.entity.Dinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Dinner, Long> {

    // First query: fetch dinners with dishes
    @Query("SELECT DISTINCT d FROM Dinner d LEFT JOIN FETCH d.dishes")
    List<Dinner> findAllWithDishes();

    // Second query: fetch dinners with available styles
    @Query("SELECT DISTINCT d FROM Dinner d LEFT JOIN FETCH d.availableStyles WHERE d IN :dinners")
    List<Dinner> findDinnersWithStyles(List<Dinner> dinners);

    // First query for single dinner: fetch dinner with dishes
    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.dishes WHERE d.id = :id")
    Optional<Dinner> findByIdWithDishes(Long id);

    // Second query for single dinner: fetch dinner with available styles
    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.availableStyles WHERE d.id = :id")
    Optional<Dinner> findByIdWithStyles(Long id);
}
