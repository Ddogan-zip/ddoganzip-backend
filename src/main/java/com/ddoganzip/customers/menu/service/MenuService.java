package com.ddoganzip.customers.menu.service;

import com.ddoganzip.customers.menu.dto.MenuDetailResponse;
import com.ddoganzip.customers.menu.dto.MenuListResponse;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.entity.Dinner;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuListResponse> getMenuList() {
        return menuRepository.findAll().stream()
                .map(dinner -> MenuListResponse.builder()
                        .id(dinner.getId())
                        .name(dinner.getName())
                        .description(dinner.getDescription())
                        .basePrice(dinner.getBasePrice())
                        .build())
                .collect(Collectors.toList());
    }

    public MenuDetailResponse getMenuDetails(Long dinnerId) {
        Dinner dinner = menuRepository.findByIdWithDetails(dinnerId)
                .orElseThrow(() -> new CustomException("Dinner not found with id: " + dinnerId));

        List<MenuDetailResponse.DishInfo> dishes = dinner.getDishes().stream()
                .map(dish -> MenuDetailResponse.DishInfo.builder()
                        .id(dish.getId())
                        .name(dish.getName())
                        .defaultQuantity(dish.getDefaultQuantity())
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

        return MenuDetailResponse.builder()
                .id(dinner.getId())
                .name(dinner.getName())
                .description(dinner.getDescription())
                .basePrice(dinner.getBasePrice())
                .dishes(dishes)
                .availableStyles(styles)
                .build();
    }
}
