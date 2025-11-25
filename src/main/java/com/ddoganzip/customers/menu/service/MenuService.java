package com.ddoganzip.customers.menu.service;

import com.ddoganzip.customers.menu.dto.MenuDetailResponse;
import com.ddoganzip.customers.menu.dto.MenuListResponse;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.customers.menu.entity.Dinner;
import com.ddoganzip.customers.menu.entity.DinnerDish;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuListResponse> getMenuList() {
        log.info("[MenuService] getMenuList() - START");
        try {
            List<Dinner> dinners = menuRepository.findAll();
            log.info("[MenuService] Found {} dinners", dinners.size());

            List<MenuListResponse> response = dinners.stream()
                    .map(dinner -> MenuListResponse.builder()
                            .id(dinner.getId())
                            .name(dinner.getName())
                            .description(dinner.getDescription())
                            .basePrice(dinner.getBasePrice())
                            .imageUrl(dinner.getImageUrl())
                            .build())
                    .collect(Collectors.toList());

            log.info("[MenuService] getMenuList() - END, returning {} menu items", response.size());
            return response;
        } catch (Exception e) {
            log.error("[MenuService] getMenuList() - ERROR: {}", e.getMessage(), e);
            throw e;
        }
    }

    public MenuDetailResponse getMenuDetails(Long dinnerId) {
        log.info("[MenuService] getMenuDetails() - START for dinnerId: {}", dinnerId);
        try {
            // Fetch dinner with dishes first
            log.debug("[MenuService] Fetching dinner with dishes for id: {}", dinnerId);
            Dinner dinner = menuRepository.findByIdWithDishes(dinnerId)
                    .orElseThrow(() -> {
                        log.error("[MenuService] Dinner not found with id: {}", dinnerId);
                        return new CustomException("Dinner not found with id: " + dinnerId);
                    });
            log.info("[MenuService] Dinner found: {}, with {} dishes", dinner.getName(), dinner.getDinnerDishes().size());

            // Fetch the same dinner with styles to populate the styles collection
            // Since we're in a transaction, this will update the same entity in the persistence context
            log.debug("[MenuService] Fetching available styles for dinner id: {}", dinnerId);
            menuRepository.findByIdWithStyles(dinnerId);
            log.info("[MenuService] Loaded {} available styles", dinner.getAvailableStyles().size());

            List<MenuDetailResponse.DishInfo> dishes = dinner.getDinnerDishes().stream()
                    .map(dinnerDish -> MenuDetailResponse.DishInfo.builder()
                            .id(dinnerDish.getDish().getId())
                            .name(dinnerDish.getDish().getName())
                            .description(dinnerDish.getDish().getDescription())
                            .basePrice(dinnerDish.getDish().getBasePrice())
                            .build())
                    .collect(Collectors.toList());

            List<MenuDetailResponse.ServingStyleInfo> styles = dinner.getAvailableStyles().stream()
                    .map(style -> MenuDetailResponse.ServingStyleInfo.builder()
                            .id(style.getId())
                            .name(style.getName())
                            .additionalPrice(style.getAdditionalPrice())
                            .description(style.getDescription())
                            .build())
                    .collect(Collectors.toList());

            MenuDetailResponse response = MenuDetailResponse.builder()
                    .id(dinner.getId())
                    .name(dinner.getName())
                    .description(dinner.getDescription())
                    .basePrice(dinner.getBasePrice())
                    .imageUrl(dinner.getImageUrl())
                    .dishes(dishes)
                    .availableStyles(styles)
                    .build();

            log.info("[MenuService] getMenuDetails() - END, returning details for dinner: {}", dinner.getName());
            return response;
        } catch (Exception e) {
            log.error("[MenuService] getMenuDetails() - ERROR for dinnerId {}: {}", dinnerId, e.getMessage(), e);
            throw e;
        }
    }
}
