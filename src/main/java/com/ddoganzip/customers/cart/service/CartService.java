package com.ddoganzip.customers.cart.service;

import com.ddoganzip.auth.entity.Customer;
import com.ddoganzip.auth.repository.AuthRepository;
import com.ddoganzip.customers.cart.dto.*;
import com.ddoganzip.customers.cart.entity.Cart;
import com.ddoganzip.customers.cart.entity.CartItem;
import com.ddoganzip.customers.cart.repository.*;
import com.ddoganzip.customers.menu.entity.Dinner;
import com.ddoganzip.customers.menu.entity.Dish;
import com.ddoganzip.customers.menu.entity.ServingStyle;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.customers.orders.entity.CustomizationAction;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final ServingStyleRepository servingStyleRepository;
    private final DishRepository dishRepository;
    private final AuthRepository authRepository;

    private Customer getCurrentCustomer() {
        log.debug("[CartService] getCurrentCustomer() - Getting authentication from SecurityContext");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[CartService] Current authenticated email: {}", email);

        log.debug("[CartService] Finding customer by email in database");
        Customer customer = authRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[CartService] Customer not found for email: {}", email);
                    return new CustomException("Customer not found");
                });

        log.info("[CartService] Customer found - ID: {}, Email: {}", customer.getId(), customer.getEmail());
        return customer;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        log.info("[CartService] getCart() - START");

        Customer customer = getCurrentCustomer();

        log.debug("[CartService] Fetching cart for customer ID: {}", customer.getId());
        Cart cart = cartRepository.findByCustomerIdWithItems(customer.getId())
                .orElseThrow(() -> {
                    log.error("[CartService] Cart not found for customer ID: {}", customer.getId());
                    return new CustomException("Cart not found");
                });

        log.info("[CartService] Cart found - Cart ID: {}, Number of items: {}", cart.getId(), cart.getItems().size());

        log.debug("[CartService] Mapping cart items to response DTOs");
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        log.debug("[CartService] Calculating total price");
        Integer totalPrice = itemResponses.stream()
                .map(CartResponse.CartItemResponse::getItemTotalPrice)
                .reduce(0, Integer::sum);

        log.info("[CartService] getCart() - SUCCESS - Total items: {}, Total price: {}",
            itemResponses.size(), totalPrice);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    public void addItemToCart(AddToCartRequest request) {
        Customer customer = getCurrentCustomer();
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new CustomException("Cart not found"));

        Dinner dinner = menuRepository.findById(request.getDinnerId())
                .orElseThrow(() -> new CustomException("Dinner not found"));

        ServingStyle servingStyle = servingStyleRepository.findById(request.getServingStyleId())
                .orElseThrow(() -> new CustomException("Serving style not found"));

        CartItem cartItem = new CartItem();
        cartItem.setDinner(dinner);
        cartItem.setServingStyle(servingStyle);
        cartItem.setQuantity(request.getQuantity());

        if (request.getCustomizations() != null) {
            for (AddToCartRequest.CustomizationRequest customizationRequest : request.getCustomizations()) {
                CustomizationAction customization = new CustomizationAction();
                customization.setCartItem(cartItem);
                customization.setAction(customizationRequest.getAction());

                if (customizationRequest.getDishId() != null) {
                    Dish dish = dishRepository.findById(customizationRequest.getDishId())
                            .orElseThrow(() -> new CustomException("Dish not found"));
                    customization.setDish(dish);
                }

                customization.setQuantity(customizationRequest.getQuantity());
                cartItem.getCustomizations().add(customization);
            }
        }

        cart.addItem(cartItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void updateItemQuantity(Long itemId, UpdateQuantityRequest request) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new CustomException("Unauthorized access to cart item");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void updateItemOptions(Long itemId, UpdateOptionsRequest request) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findByIdWithCustomizations(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new CustomException("Unauthorized access to cart item");
        }

        ServingStyle servingStyle = servingStyleRepository.findById(request.getServingStyleId())
                .orElseThrow(() -> new CustomException("Serving style not found"));

        cartItem.setServingStyle(servingStyle);
        cartItem.getCustomizations().clear();

        if (request.getCustomizations() != null) {
            for (AddToCartRequest.CustomizationRequest customizationRequest : request.getCustomizations()) {
                CustomizationAction customization = new CustomizationAction();
                customization.setCartItem(cartItem);
                customization.setAction(customizationRequest.getAction());

                if (customizationRequest.getDishId() != null) {
                    Dish dish = dishRepository.findById(customizationRequest.getDishId())
                            .orElseThrow(() -> new CustomException("Dish not found"));
                    customization.setDish(dish);
                }

                customization.setQuantity(customizationRequest.getQuantity());
                cartItem.getCustomizations().add(customization);
            }
        }

        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeItem(Long itemId) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new CustomException("Unauthorized access to cart item");
        }

        Cart cart = cartItem.getCart();
        cart.removeItem(cartItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void customizeCartItem(Long itemId, CustomizeCartItemRequest request) {
        Customer customer = getCurrentCustomer();
        CartItem cartItem = cartItemRepository.findByIdWithCustomizations(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new CustomException("Unauthorized access to cart item");
        }

        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new CustomException("Dish not found"));

        String action = request.getAction().toUpperCase();

        if ("ADD".equals(action) || "REPLACE".equals(action)) {
            // ADD or REPLACE: 기존 커스터마이징을 찾아서 수정하거나 새로 추가
            CustomizationAction existing = cartItem.getCustomizations().stream()
                    .filter(c -> c.getDish() != null && c.getDish().getId().equals(dish.getId()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setAction(action);
                existing.setQuantity(request.getQuantity());
            } else {
                CustomizationAction newCustomization = new CustomizationAction();
                newCustomization.setCartItem(cartItem);
                newCustomization.setAction(action);
                newCustomization.setDish(dish);
                newCustomization.setQuantity(request.getQuantity());
                cartItem.getCustomizations().add(newCustomization);
            }
        } else if ("REMOVE".equals(action)) {
            // REMOVE: 해당 dish의 커스터마이징을 제거
            cartItem.getCustomizations().removeIf(c ->
                    c.getDish() != null && c.getDish().getId().equals(dish.getId()));
        } else {
            throw new CustomException("Invalid action: " + action);
        }

        cartItemRepository.save(cartItem);
    }

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem item) {
        Integer itemPrice = (item.getDinner().getBasePrice() + item.getServingStyle().getAdditionalPrice())
                * item.getQuantity();

        List<CartResponse.CustomizationResponse> customizationResponses = item.getCustomizations().stream()
                .map(c -> CartResponse.CustomizationResponse.builder()
                        .action(c.getAction())
                        .dishId(c.getDish() != null ? c.getDish().getId() : null)
                        .dishName(c.getDish() != null ? c.getDish().getName() : null)
                        .quantity(c.getQuantity())
                        .pricePerUnit(c.getDish() != null ? c.getDish().getBasePrice() : 0)
                        .build())
                .collect(Collectors.toList());

        return CartResponse.CartItemResponse.builder()
                .itemId(item.getId())
                .dinnerId(item.getDinner().getId())
                .dinnerName(item.getDinner().getName())
                .dinnerBasePrice(item.getDinner().getBasePrice())
                .servingStyleId(item.getServingStyle().getId())
                .servingStyleName(item.getServingStyle().getName())
                .servingStylePrice(item.getServingStyle().getAdditionalPrice())
                .quantity(item.getQuantity())
                .itemTotalPrice(itemPrice)
                .customizations(customizationResponses)
                .build();
    }
}
