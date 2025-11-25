package com.ddoganzip.customers.orders.service;

import com.ddoganzip.auth.entity.Customer;
import com.ddoganzip.auth.repository.AuthRepository;
import com.ddoganzip.customers.cart.entity.Cart;
import com.ddoganzip.customers.cart.entity.CartItem;
import com.ddoganzip.customers.cart.repository.CartRepository;
import com.ddoganzip.customers.menu.repository.MenuRepository;
import com.ddoganzip.customers.orders.dto.*;
import com.ddoganzip.customers.orders.entity.CustomizationAction;
import com.ddoganzip.customers.orders.entity.Order;
import com.ddoganzip.customers.orders.entity.OrderItem;
import com.ddoganzip.customers.orders.entity.OrderStatus;
import com.ddoganzip.customers.orders.repository.OrderRepository;
import com.ddoganzip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthRepository authRepository;
    private final MenuRepository menuRepository;

    private Customer getCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Customer not found"));
    }

    @Transactional
    public Long checkout(CheckoutRequest request) {
        Customer customer = getCurrentCustomer();
        Cart cart = cartRepository.findByCustomerIdWithItems(customer.getId())
                .orElseThrow(() -> new CustomException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CustomException("Cart is empty");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(request.getDeliveryDate());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(OrderStatus.CHECKING_STOCK);

        Integer totalPrice = 0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDinner(cartItem.getDinner());
            orderItem.setServingStyle(cartItem.getServingStyle());
            orderItem.setQuantity(cartItem.getQuantity());

            // 기본 가격: (디너 기본가 + 서빙스타일 추가가) * 수량
            Integer itemPrice = (cartItem.getDinner().getBasePrice() + cartItem.getServingStyle().getAdditionalPrice())
                    * cartItem.getQuantity();

            // Customization 가격 계산 및 복사
            for (CustomizationAction cartCustomization : cartItem.getCustomizations()) {
                CustomizationAction orderCustomization = new CustomizationAction();
                orderCustomization.setOrderItem(orderItem);
                orderCustomization.setAction(cartCustomization.getAction());
                orderCustomization.setDish(cartCustomization.getDish());
                orderCustomization.setQuantity(cartCustomization.getQuantity());
                orderItem.getCustomizations().add(orderCustomization);

                // ADD 액션일 경우 가격 추가
                if ("ADD".equals(cartCustomization.getAction()) && cartCustomization.getDish() != null) {
                    itemPrice += cartCustomization.getDish().getBasePrice() * cartCustomization.getQuantity();
                }
            }

            orderItem.setPrice(itemPrice);
            totalPrice += itemPrice;

            order.getItems().add(orderItem);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        cart.clearItems();
        cartRepository.save(cart);

        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory() {
        Customer customer = getCurrentCustomer();
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customer.getId());

        return orders.stream()
                .map(order -> OrderHistoryResponse.builder()
                        .orderId(order.getId())
                        .orderDate(order.getOrderDate())
                        .deliveryDate(order.getDeliveryDate())
                        .deliveryAddress(order.getDeliveryAddress())
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .itemCount(order.getItems().size())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetails(Long orderId) {
        Customer customer = getCurrentCustomer();
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new CustomException("Unauthorized access to order");
        }

        List<OrderDetailResponse.OrderItemInfo> itemInfos = order.getItems().stream()
                .map(item -> {
                    // 커스터마이징 정보 매핑 (가격 정보 포함)
                    List<OrderDetailResponse.CustomizationInfo> customizations = item.getCustomizations().stream()
                            .map(c -> OrderDetailResponse.CustomizationInfo.builder()
                                    .action(c.getAction())
                                    .dishId(c.getDish() != null ? c.getDish().getId() : null)
                                    .dishName(c.getDish() != null ? c.getDish().getName() : null)
                                    .quantity(c.getQuantity())
                                    .pricePerUnit(c.getDish() != null ? c.getDish().getBasePrice() : 0)
                                    .build())
                            .collect(Collectors.toList());

                    // 기본 구성 품목 정보 매핑
                    List<OrderDetailResponse.BaseDishInfo> baseDishes = menuRepository
                            .findByIdWithDishes(item.getDinner().getId())
                            .map(dinner -> dinner.getDinnerDishes().stream()
                                    .map(dinnerDish -> OrderDetailResponse.BaseDishInfo.builder()
                                            .dishId(dinnerDish.getDish().getId())
                                            .dishName(dinnerDish.getDish().getName())
                                            .quantity(dinnerDish.getQuantity())
                                            .build())
                                    .collect(Collectors.toList()))
                            .orElse(List.of());

                    return OrderDetailResponse.OrderItemInfo.builder()
                            .itemId(item.getId())
                            .dinnerId(item.getDinner().getId())
                            .dinnerName(item.getDinner().getName())
                            .servingStyleName(item.getServingStyle().getName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .baseDishes(baseDishes)
                            .customizations(customizations)
                            .build();
                })
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .items(itemInfos)
                .build();
    }
}
