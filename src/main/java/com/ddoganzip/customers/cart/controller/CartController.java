package com.ddoganzip.customers.cart.controller;

import com.ddoganzip.common.ApiResponse;
import com.ddoganzip.customers.cart.dto.*;
import com.ddoganzip.customers.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addItemToCart(request);
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        cartService.updateItemQuantity(itemId, request);
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}/options")
    public ResponseEntity<CartResponse> updateItemOptions(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateOptionsRequest request) {
        cartService.updateItemOptions(itemId, request);
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items/{itemId}/customize")
    public ResponseEntity<CartResponse> customizeCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CustomizeCartItemRequest request) {
        cartService.customizeCartItem(itemId, request);
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }
}
