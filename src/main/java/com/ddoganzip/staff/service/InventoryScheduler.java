package com.ddoganzip.staff.service;

import com.ddoganzip.customers.cart.repository.DishRepository;
import com.ddoganzip.customers.menu.entity.Dish;
import com.ddoganzip.customers.menu.entity.DishCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryScheduler {

    private final DishRepository dishRepository;

    private static final int LIQUOR_REPLENISH_AMOUNT = 10;  // 주류 충전량 (병)

    /**
     * 매일 아침 8시에 주류 재고 충전
     * 주류 종류별 10병씩 추가
     */
    @Scheduled(cron = "0 0 8 * * *")  // 매일 08:00
    @Transactional
    public void replenishLiquor() {
        log.info("=== [InventoryScheduler] Daily liquor replenishment - START ===");

        List<Dish> liquorDishes = dishRepository.findAll().stream()
                .filter(dish -> dish.getCategory() == DishCategory.LIQUOR)
                .toList();

        for (Dish dish : liquorDishes) {
            Integer currentStock = dish.getCurrentStock() != null ? dish.getCurrentStock() : 0;
            Integer newStock = currentStock + LIQUOR_REPLENISH_AMOUNT;
            dish.setCurrentStock(newStock);
            dishRepository.save(dish);

            log.info("[InventoryScheduler] Liquor replenished - {}: {} -> {} (+{})",
                    dish.getName(), currentStock, newStock, LIQUOR_REPLENISH_AMOUNT);
        }

        log.info("=== [InventoryScheduler] Daily liquor replenishment - END ({} items) ===",
                liquorDishes.size());
    }

    /**
     * 화요일과 금요일 오후 3시 30분에 일반 재고 충전
     * 주류 제외, 장식품 제외
     * minimum_stock 만큼 채움
     */
    @Scheduled(cron = "0 30 15 * * TUE,FRI")  // 화요일, 금요일 15:30
    @Transactional
    public void replenishGeneralInventory() {
        log.info("=== [InventoryScheduler] Tue/Fri general inventory replenishment - START ===");

        List<Dish> generalDishes = dishRepository.findAll().stream()
                .filter(dish -> dish.getCategory() == DishCategory.GENERAL)
                .toList();

        int replenishedCount = 0;
        for (Dish dish : generalDishes) {
            Integer currentStock = dish.getCurrentStock() != null ? dish.getCurrentStock() : 0;
            Integer minimumStock = dish.getMinimumStock() != null ? dish.getMinimumStock() : 10;

            // minimum_stock보다 적으면 minimum_stock까지 채움
            if (currentStock < minimumStock) {
                Integer oldStock = currentStock;
                dish.setCurrentStock(minimumStock);
                dishRepository.save(dish);
                replenishedCount++;

                log.info("[InventoryScheduler] General item replenished - {}: {} -> {}",
                        dish.getName(), oldStock, minimumStock);
            }
        }

        log.info("=== [InventoryScheduler] Tue/Fri general inventory replenishment - END ({}/{} items replenished) ===",
                replenishedCount, generalDishes.size());
    }
}
