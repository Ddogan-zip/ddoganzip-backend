package com.ddoganzip.customers.orders.repository;

import com.ddoganzip.entity.Order;
import com.ddoganzip.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items oi LEFT JOIN FETCH oi.dinner LEFT JOIN FETCH oi.servingStyle WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(Long orderId);

    @Query("SELECT o FROM Order o WHERE o.status NOT IN :statuses ORDER BY o.orderDate DESC")
    List<Order> findActiveOrders(List<OrderStatus> statuses);
}
