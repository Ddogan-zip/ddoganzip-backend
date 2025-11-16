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
    public ResponseEntity<ApiResponse<Void>> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addItemToCart(request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart"));
    }

    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<ApiResponse<Void>> updateItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        cartService.updateItemQuantity(itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Item quantity updated"));
    }

    @PutMapping("/items/{itemId}/options")
    public ResponseEntity<ApiResponse<Void>> updateItemOptions(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateOptionsRequest request) {
        cartService.updateItemOptions(itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Item options updated"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart"));
    }
}
