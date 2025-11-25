package com.ddoganzip.staff.service;

import com.ddoganzip.customers.cart.repository.DishRepository;
import com.ddoganzip.customers.menu.entity.Dish;
import com.ddoganzip.customers.menu.entity.Dinner;
import com.ddoganzip.customers.menu.entity.DinnerDish;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.customers.orders.repository.OrderRepository;
import com.ddoganzip.customers.orders.entity.Order;
import com.ddoganzip.customers.orders.entity.OrderItem;
import com.ddoganzip.customers.orders.entity.OrderStatus;
import com.ddoganzip.exception.CustomException;
import com.ddoganzip.staff.dto.ActiveOrdersResponse;
import com.ddoganzip.staff.dto.InventoryItemResponse;
import com.ddoganzip.staff.dto.OrderInventoryCheckResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<ActiveOrdersResponse> getActiveOrders() {
        log.info("[StaffService] getActiveOrders() - START");
        List<Order> activeOrders = orderRepository.findActiveOrders(
                Arrays.asList(OrderStatus.DELIVERED)
        );
        log.info("[StaffService] Found {} active orders", activeOrders.size());

        List<ActiveOrdersResponse> response = activeOrders.stream()
                .map(order -> ActiveOrdersResponse.builder()
                        .orderId(order.getId())
                        .customerName(order.getCustomer().getName())
                        .customerEmail(order.getCustomer().getEmail())
                        .orderDate(order.getOrderDate())
                        .deliveryDate(order.getDeliveryDate())
                        .deliveryAddress(order.getDeliveryAddress())
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .itemCount(order.getItems().size())
                        .build())
                .collect(Collectors.toList());

        log.info("[StaffService] getActiveOrders() - END");
        return response;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("[StaffService] updateOrderStatus() - START for orderId: {}, newStatus: {}",
            orderId, request.getStatus());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[StaffService] Order not found with id: {}", orderId);
                    return new CustomException("Order not found");
                });

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        log.info("[StaffService] Order {} status updated from {} to {}",
            orderId, oldStatus, request.getStatus());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventory() {
        log.info("[StaffService] getInventory() - START");
        List<Dish> dishes = dishRepository.findAll();
        log.info("[StaffService] Found {} dishes in inventory", dishes.size());

        List<InventoryItemResponse> inventory = dishes.stream()
                .map(dish -> {
                    boolean isLowStock = dish.getCurrentStock() != null &&
                                       dish.getMinimumStock() != null &&
                                       dish.getCurrentStock() < dish.getMinimumStock();
                    if (isLowStock) {
                        log.warn("[StaffService] LOW STOCK WARNING - Dish: {}, Current: {}, Minimum: {}",
                            dish.getName(), dish.getCurrentStock(), dish.getMinimumStock());
                    }
                    return InventoryItemResponse.builder()
                            .dishId(dish.getId())
                            .dishName(dish.getName())
                            .currentStock(dish.getCurrentStock() != null ? dish.getCurrentStock() : 0)
                            .minimumStock(dish.getMinimumStock() != null ? dish.getMinimumStock() : 10)
                            .build();
                })
                .collect(Collectors.toList());

        log.info("[StaffService] getInventory() - END, returning {} items", inventory.size());
        return inventory;
    }

    @Transactional(readOnly = true)
    public OrderInventoryCheckResponse checkOrderInventory(Long orderId) {
        log.info("[StaffService] checkOrderInventory() - START for orderId: {}", orderId);

        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[StaffService] Order not found with id: {}", orderId);
                    return new CustomException("Order not found");
                });

        log.info("[StaffService] Order found - Customer: {}, Items: {}",
            order.getCustomer().getName(), order.getItems().size());

        // 2. 주문에 필요한 각 dish의 수량을 계산 (Map<dishId, requiredQuantity>)
        Map<Long, Integer> dishRequirements = new HashMap<>();

        for (OrderItem orderItem : order.getItems()) {
            Long dinnerId = orderItem.getDinner().getId();
            Integer orderQuantity = orderItem.getQuantity();

            log.debug("[StaffService] Processing OrderItem - Dinner ID: {}, Quantity: {}",
                dinnerId, orderQuantity);

            // Dinner의 dinnerDishes를 조회 (DinnerDish를 통해 수량 정보 포함)
            Dinner dinner = menuRepository.findByIdWithDishes(dinnerId)
                    .orElseThrow(() -> new CustomException("Dinner not found"));

            // 각 dish의 필요 수량 계산 (DinnerDish의 quantity 사용)
            for (DinnerDish dinnerDish : dinner.getDinnerDishes()) {
                Dish dish = dinnerDish.getDish();
                Integer dishQuantityInDinner = dinnerDish.getQuantity(); // Dinner에 포함된 dish의 기본 수량
                Integer requiredQty = dishQuantityInDinner * orderQuantity;

                // 같은 dish가 여러 dinner에 포함될 수 있으므로 합산
                dishRequirements.merge(dish.getId(), requiredQty, Integer::sum);

                log.debug("[StaffService] Dish: {} - Quantity in dinner: {}, Order qty: {}, Required: {}",
                    dish.getName(), dishQuantityInDinner, orderQuantity, requiredQty);
            }
        }

        log.info("[StaffService] Total unique dishes required: {}", dishRequirements.size());

        // 3. 각 dish의 현재 재고와 비교
        List<OrderInventoryCheckResponse.DishRequirement> requirements = new ArrayList<>();
        boolean allSufficient = true;

        for (Map.Entry<Long, Integer> entry : dishRequirements.entrySet()) {
            Long dishId = entry.getKey();
            Integer requiredQty = entry.getValue();

            Dish dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new CustomException("Dish not found"));

            Integer currentStock = dish.getCurrentStock() != null ? dish.getCurrentStock() : 0;
            boolean isSufficient = currentStock >= requiredQty;
            Integer shortage = requiredQty - currentStock;  // 양수면 부족, 음수면 충분

            if (!isSufficient) {
                allSufficient = false;
                log.warn("[StaffService] INSUFFICIENT STOCK - Dish: {}, Required: {}, Current: {}, Shortage: {}",
                    dish.getName(), requiredQty, currentStock, shortage);
            } else {
                log.debug("[StaffService] SUFFICIENT - Dish: {}, Required: {}, Current: {}",
                    dish.getName(), requiredQty, currentStock);
            }

            requirements.add(OrderInventoryCheckResponse.DishRequirement.builder()
                    .dishId(dishId)
                    .dishName(dish.getName())
                    .requiredQuantity(requiredQty)
                    .currentStock(currentStock)
                    .isSufficient(isSufficient)
                    .shortage(shortage > 0 ? shortage : 0)  // 0 이하면 0으로 (충분함)
                    .build());
        }

        // 4. 응답 생성
        OrderInventoryCheckResponse response = OrderInventoryCheckResponse.builder()
                .orderId(orderId)
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .isSufficient(allSufficient)
                .requiredItems(requirements)
                .build();

        log.info("[StaffService] checkOrderInventory() - END, isSufficient: {}, items: {}",
            allSufficient, requirements.size());

        return response;
    }
}
