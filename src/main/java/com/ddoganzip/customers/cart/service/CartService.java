package com.ddoganzip.customers.cart.service;

import com.ddoganzip.auth.repository.AuthRepository;
import com.ddoganzip.customers.cart.dto.*;
import com.ddoganzip.customers.cart.repository.*;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.entity.*;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        Customer customer = getCurrentCustomer();
        Cart cart = cartRepository.findByCustomerIdWithItems(customer.getId())
                .orElseThrow(() -> new CustomException("Cart not found"));

        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        Integer totalPrice = itemResponses.stream()
                .map(CartResponse.CartItemResponse::getItemTotalPrice)
                .reduce(0, Integer::sum);

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

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem item) {
        Integer itemPrice = (item.getDinner().getBasePrice() + item.getServingStyle().getAdditionalPrice())
                * item.getQuantity();

        List<CartResponse.CustomizationResponse> customizationResponses = item.getCustomizations().stream()
                .map(c -> CartResponse.CustomizationResponse.builder()
                        .action(c.getAction())
                        .dishId(c.getDish() != null ? c.getDish().getId() : null)
                        .dishName(c.getDish() != null ? c.getDish().getName() : null)
                        .quantity(c.getQuantity())
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
