package com.ddoganzip.staff.service;

import com.ddoganzip.customers.cart.repository.DishRepository;
import com.ddoganzip.customers.menu.entity.Dish;
import com.ddoganzip.customers.menu.entity.DishCategory;
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
import com.ddoganzip.staff.dto.StaffAvailabilityResponse;
import com.ddoganzip.staff.dto.UpdateOrderStatusRequest;
import com.ddoganzip.staff.entity.StaffAvailability;
import com.ddoganzip.staff.repository.StaffAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;
    private final StaffAvailabilityRepository staffAvailabilityRepository;

    @Transactional(readOnly = true)
    public List<ActiveOrdersResponse> getActiveOrders() {
        log.info("[StaffService] getActiveOrders() - START");
        List<Order> activeOrders = orderRepository.findActiveOrders(
                Arrays.asList(OrderStatus.DRIVER_RETURNED)
        );
        log.info("[StaffService] Found {} active orders", activeOrders.size());

        List<ActiveOrdersResponse> response = activeOrders.stream()
                .map(order -> ActiveOrdersResponse.builder()
                        .orderId(order.getId())
                        .customerName(order.getCustomer().getName())
                        .customerEmail(order.getCustomer().getEmail())
                        .orderDate(order.getOrderDate())
                        .deliveryDate(order.getDeliveryDate())
                        .deliveredAt(order.getDeliveredAt())
                        .deliveryAddress(order.getDeliveryAddress())
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .itemCount(order.getItems().size())
                        .build())
                .collect(Collectors.toList());

        log.info("[StaffService] getActiveOrders() - END");
        return response;
    }

    @Transactional(readOnly = true)
    public StaffAvailabilityResponse getStaffAvailability() {
        log.info("[StaffService] getStaffAvailability() - START");
        StaffAvailability sa = staffAvailabilityRepository.getStaffAvailability();

        StaffAvailabilityResponse response = StaffAvailabilityResponse.builder()
                .availableCooks(sa.getAvailableCooks())
                .totalCooks(sa.getTotalCooks())
                .availableDrivers(sa.getAvailableDrivers())
                .totalDrivers(sa.getTotalDrivers())
                .canStartCooking(sa.getAvailableCooks() > 0)
                .canStartDelivery(sa.getAvailableDrivers() > 0)
                .build();

        log.info("[StaffService] getStaffAvailability() - END, cooks: {}/{}, drivers: {}/{}",
                sa.getAvailableCooks(), sa.getTotalCooks(),
                sa.getAvailableDrivers(), sa.getTotalDrivers());
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
        OrderStatus newStatus = request.getStatus();
        StaffAvailability sa = staffAvailabilityRepository.getStaffAvailability();

        // RECEIVED(접수)로 변경될 때 재고 차감
        if (newStatus == OrderStatus.RECEIVED && oldStatus != OrderStatus.RECEIVED) {
            deductInventoryForOrder(order);
        }

        // IN_KITCHEN(조리중)으로 변경될 때 요리 담당 직원 체크 및 할당
        if (newStatus == OrderStatus.IN_KITCHEN && oldStatus != OrderStatus.IN_KITCHEN) {
            if (sa.getAvailableCooks() <= 0) {
                throw new CustomException("No available cook. Please wait until a cook is available.");
            }
            sa.setAvailableCooks(sa.getAvailableCooks() - 1);
            staffAvailabilityRepository.save(sa);
            log.info("[StaffService] Cook assigned. Available cooks: {}/{}",
                    sa.getAvailableCooks(), sa.getTotalCooks());
        }

        // COOKED(조리완료)로 변경될 때 요리 담당 직원 복귀
        if (newStatus == OrderStatus.COOKED && oldStatus == OrderStatus.IN_KITCHEN) {
            sa.setAvailableCooks(sa.getAvailableCooks() + 1);
            staffAvailabilityRepository.save(sa);
            log.info("[StaffService] Cook returned. Available cooks: {}/{}",
                    sa.getAvailableCooks(), sa.getTotalCooks());
        }

        // DELIVERING(배달중)으로 변경될 때 배달 담당 직원 체크 및 할당
        if (newStatus == OrderStatus.DELIVERING && oldStatus != OrderStatus.DELIVERING) {
            if (sa.getAvailableDrivers() <= 0) {
                throw new CustomException("No available driver. Please wait until a driver is available.");
            }
            sa.setAvailableDrivers(sa.getAvailableDrivers() - 1);
            staffAvailabilityRepository.save(sa);
            log.info("[StaffService] Driver assigned. Available drivers: {}/{}",
                    sa.getAvailableDrivers(), sa.getTotalDrivers());
        }

        // DELIVERED(배달완료)로 변경될 때 배달완료 시각 기록
        if (newStatus == OrderStatus.DELIVERED && oldStatus != OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        log.info("[StaffService] Order {} status updated from {} to {}",
            orderId, oldStatus, newStatus);
    }

    @Transactional
    public void driverReturn(Long orderId) {
        log.info("[StaffService] driverReturn() - START for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new CustomException("Driver can only return from DELIVERED orders. Current status: " + order.getStatus());
        }

        // 주문 상태를 DRIVER_RETURNED로 변경
        order.setStatus(OrderStatus.DRIVER_RETURNED);
        orderRepository.save(order);

        // 기사 가용성 증가
        StaffAvailability sa = staffAvailabilityRepository.getStaffAvailability();

        if (sa.getAvailableDrivers() >= sa.getTotalDrivers()) {
            throw new CustomException("All drivers are already available.");
        }

        sa.setAvailableDrivers(sa.getAvailableDrivers() + 1);
        staffAvailabilityRepository.save(sa);
        log.info("[StaffService] Driver returned for orderId: {}. Order status: DRIVER_RETURNED. Available drivers: {}/{}",
                orderId, sa.getAvailableDrivers(), sa.getTotalDrivers());
    }

    private void deductInventoryForOrder(Order order) {
        log.info("[StaffService] deductInventoryForOrder() - START for orderId: {}", order.getId());

        Map<Long, Integer> dishRequirements = new HashMap<>();

        for (OrderItem orderItem : order.getItems()) {
            Integer orderQuantity = orderItem.getQuantity();

            // 1. Dinner에 포함된 기본 dish들
            Dinner dinner = menuRepository.findByIdWithDishes(orderItem.getDinner().getId())
                    .orElseThrow(() -> new CustomException("Dinner not found"));

            for (DinnerDish dinnerDish : dinner.getDinnerDishes()) {
                Long dishId = dinnerDish.getDish().getId();
                Integer requiredQty = dinnerDish.getQuantity() * orderQuantity;
                dishRequirements.merge(dishId, requiredQty, Integer::sum);
            }

            // 2. Customization에서 ADD된 dish들
            for (var customization : orderItem.getCustomizations()) {
                if ("ADD".equals(customization.getAction()) && customization.getDish() != null) {
                    Long dishId = customization.getDish().getId();
                    Integer requiredQty = customization.getQuantity() * orderQuantity;
                    dishRequirements.merge(dishId, requiredQty, Integer::sum);
                }
            }
        }

        // 재고 차감 (DECORATION 카테고리는 제외)
        for (Map.Entry<Long, Integer> entry : dishRequirements.entrySet()) {
            Long dishId = entry.getKey();
            Integer requiredQty = entry.getValue();

            Dish dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new CustomException("Dish not found: " + dishId));

            // 장식품은 재고 차감 제외
            if (dish.getCategory() == DishCategory.DECORATION) {
                log.info("[StaffService] Skipping decoration - Dish: {}", dish.getName());
                continue;
            }

            Integer currentStock = dish.getCurrentStock() != null ? dish.getCurrentStock() : 0;
            Integer newStock = currentStock - requiredQty;

            dish.setCurrentStock(newStock);
            dishRepository.save(dish);

            log.info("[StaffService] Deducted stock - Dish: {}, Before: {}, After: {}, Deducted: {}",
                dish.getName(), currentStock, newStock, requiredQty);
        }

        log.info("[StaffService] deductInventoryForOrder() - END");
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventory() {
        log.info("[StaffService] getInventory() - START");
        List<Dish> dishes = dishRepository.findAll();
        log.info("[StaffService] Found {} dishes in inventory", dishes.size());

        // 장식품(DECORATION)은 재고 목록에서 제외
        List<InventoryItemResponse> inventory = dishes.stream()
                .filter(dish -> dish.getCategory() != DishCategory.DECORATION)
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
                Integer dishQuantityInDinner = dinnerDish.getQuantity();
                Integer requiredQty = dishQuantityInDinner * orderQuantity;
                dishRequirements.merge(dish.getId(), requiredQty, Integer::sum);

                log.debug("[StaffService] Dish: {} - Quantity in dinner: {}, Order qty: {}, Required: {}",
                    dish.getName(), dishQuantityInDinner, orderQuantity, requiredQty);
            }
        }

        log.info("[StaffService] Total unique dishes required: {}", dishRequirements.size());

        // 3. 각 dish의 현재 재고와 비교 (DECORATION은 항상 충분으로 처리)
        List<OrderInventoryCheckResponse.DishRequirement> requirements = new ArrayList<>();
        boolean allSufficient = true;

        for (Map.Entry<Long, Integer> entry : dishRequirements.entrySet()) {
            Long dishId = entry.getKey();
            Integer requiredQty = entry.getValue();

            Dish dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new CustomException("Dish not found"));

            // 장식품은 재고 체크 제외 (항상 충분)
            if (dish.getCategory() == DishCategory.DECORATION) {
                continue;
            }

            Integer currentStock = dish.getCurrentStock() != null ? dish.getCurrentStock() : 0;
            boolean isSufficient = currentStock >= requiredQty;
            Integer shortage = requiredQty - currentStock;

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
                    .shortage(shortage > 0 ? shortage : 0)
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
