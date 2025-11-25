package com.ddoganzip.customers.menu.repository;

import com.ddoganzip.customers.menu.entity.Dinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Dinner, Long> {

    // First query: fetch dinners with dinner_dishes
    @Query("SELECT DISTINCT d FROM Dinner d LEFT JOIN FETCH d.dinnerDishes")
    List<Dinner> findAllWithDishes();

    // Second query: fetch dinners with available styles
    @Query("SELECT DISTINCT d FROM Dinner d LEFT JOIN FETCH d.availableStyles WHERE d IN :dinners")
    List<Dinner> findDinnersWithStyles(List<Dinner> dinners);

    // Fetch dinner with dinner_dishes and nested dish
    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.dinnerDishes dd LEFT JOIN FETCH dd.dish WHERE d.id = :id")
    Optional<Dinner> findByIdWithDishes(Long id);

    // Second query for single dinner: fetch dinner with available styles
    @Query("SELECT d FROM Dinner d LEFT JOIN FETCH d.availableStyles WHERE d.id = :id")
    Optional<Dinner> findByIdWithStyles(Long id);
}
