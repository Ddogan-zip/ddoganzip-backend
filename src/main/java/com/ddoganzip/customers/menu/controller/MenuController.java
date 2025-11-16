package com.ddoganzip.customers.menu.controller;

import com.ddoganzip.customers.menu.dto.MenuDetailResponse;
import com.ddoganzip.customers.menu.dto.MenuListResponse;
import com.ddoganzip.customers.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/list")
    public ResponseEntity<List<MenuListResponse>> getMenuList() {
        List<MenuListResponse> menuList = menuService.getMenuList();
        return ResponseEntity.ok(menuList);
    }

    @GetMapping("/details/{dinnerId}")
    public ResponseEntity<MenuDetailResponse> getMenuDetails(@PathVariable Long dinnerId) {
        MenuDetailResponse details = menuService.getMenuDetails(dinnerId);
        return ResponseEntity.ok(details);
    }
}
