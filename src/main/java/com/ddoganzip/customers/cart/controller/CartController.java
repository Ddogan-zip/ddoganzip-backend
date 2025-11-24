package com.ddoganzip.customers.cart.controller;

import com.ddoganzip.common.ApiResponse;
import com.ddoganzip.customers.cart.dto.*;
import com.ddoganzip.customers.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        log.info("=== [CartController] GET /api/cart - START ===");
        try {
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Cart retrieved successfully - Total items: {}, Total price: {}",
                cart.getItems().size(), cart.getTotalPrice());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to get cart, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] GET /api/cart - END ===");
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        log.info("=== [CartController] POST /api/cart/items - START ===");
        log.info("[CartController] Adding item to cart - DinnerId: {}, ServingStyleId: {}, Quantity: {}",
            request.getDinnerId(), request.getServingStyleId(), request.getQuantity());
        try {
            cartService.addItemToCart(request);
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Item added successfully");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to add item to cart, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] POST /api/cart/items - END ===");
        }
    }

    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        log.info("=== [CartController] PUT /api/cart/items/{}/quantity - START ===", itemId);
        log.info("[CartController] Updating quantity - ItemId: {}, NewQuantity: {}", itemId, request.getQuantity());
        try {
            cartService.updateItemQuantity(itemId, request);
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Quantity updated successfully");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to update quantity, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] PUT /api/cart/items/{}/quantity - END ===", itemId);
        }
    }

    @PutMapping("/items/{itemId}/options")
    public ResponseEntity<CartResponse> updateItemOptions(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateOptionsRequest request) {
        log.info("=== [CartController] PUT /api/cart/items/{}/options - START ===", itemId);
        try {
            cartService.updateItemOptions(itemId, request);
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Options updated successfully");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to update options, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] PUT /api/cart/items/{}/options - END ===", itemId);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        log.info("=== [CartController] DELETE /api/cart/items/{} - START ===", itemId);
        try {
            cartService.removeItem(itemId);
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Item removed successfully");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to remove item, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] DELETE /api/cart/items/{} - END ===", itemId);
        }
    }

    @PostMapping("/items/{itemId}/customize")
    public ResponseEntity<CartResponse> customizeCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CustomizeCartItemRequest request) {
        log.info("=== [CartController] POST /api/cart/items/{}/customize - START ===", itemId);
        log.info("[CartController] Customizing item - ItemId: {}, Action: {}, DishId: {}",
            itemId, request.getAction(), request.getDishId());
        try {
            cartService.customizeCartItem(itemId, request);
            CartResponse cart = cartService.getCart();
            log.info("[CartController] Item customized successfully");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("[CartController] Failed to customize item, Error: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== [CartController] POST /api/cart/items/{}/customize - END ===", itemId);
        }
    }
}
