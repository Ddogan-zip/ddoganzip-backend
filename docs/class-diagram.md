# Class Diagrams

This document contains detailed Mermaid class diagrams for the DDogan Zip backend system.

---

## 1. Entity Class Diagram (Domain Model)

### 1.1 Core Entities & Relationships

```mermaid
classDiagram
    direction TB

    %% Base Classes
    class BaseEntity {
        <<abstract>>
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }

    %% Auth Domain
    class Customer {
        -Long id
        -String email
        -String password
        -String name
        -String address
        -String phone
        -Role role
        -Cart cart
        -List~Order~ orders
    }

    class Role {
        <<enumeration>>
        USER
        STAFF
    }

    %% Cart Domain
    class Cart {
        -Long id
        -Customer customer
        -List~CartItem~ items
        +addItem(CartItem) void
        +removeItem(CartItem) void
        +clearItems() void
    }

    class CartItem {
        -Long id
        -Cart cart
        -Dinner dinner
        -ServingStyle servingStyle
        -Integer quantity
        -List~CustomizationAction~ customizations
    }

    %% Menu Domain
    class Dinner {
        -Long id
        -String name
        -String description
        -Integer basePrice
        -String imageUrl
        -List~DinnerDish~ dinnerDishes
        -List~ServingStyle~ availableStyles
    }

    class Dish {
        -Long id
        -String name
        -String description
        -Integer basePrice
        -Integer defaultQuantity
        -Integer currentStock
        -Integer minimumStock
        -DishCategory category
    }

    class DishCategory {
        <<enumeration>>
        GENERAL
        LIQUOR
        DECORATION
    }

    class DinnerDish {
        -Long id
        -Dinner dinner
        -Dish dish
        -Integer quantity
    }

    class ServingStyle {
        -Long id
        -String name
        -Integer additionalPrice
        -String description
    }

    %% Order Domain
    class Order {
        -Long id
        -Customer customer
        -LocalDateTime orderDate
        -LocalDateTime deliveryDate
        -LocalDateTime deliveredAt
        -String deliveryAddress
        -OrderStatus status
        -Integer totalPrice
        -List~OrderItem~ items
    }

    class OrderStatus {
        <<enumeration>>
        CHECKING_STOCK
        RECEIVED
        IN_KITCHEN
        COOKED
        DELIVERING
        DELIVERED
        DRIVER_RETURNED
    }

    class OrderItem {
        -Long id
        -Order order
        -Dinner dinner
        -ServingStyle servingStyle
        -Integer quantity
        -Integer price
        -List~CustomizationAction~ customizations
    }

    class CustomizationAction {
        -Long id
        -CartItem cartItem
        -OrderItem orderItem
        -String action
        -Dish dish
        -Integer quantity
    }

    %% Staff Domain
    class StaffAvailability {
        -Long id
        -Integer availableCooks
        -Integer availableDrivers
        -Integer totalCooks
        -Integer totalDrivers
    }

    %% Inheritance
    Customer --|> BaseEntity
    Order --|> BaseEntity

    %% Relationships - Customer
    Customer "1" --> "1" Cart : has
    Customer "1" --> "*" Order : places
    Customer --> Role : has

    %% Relationships - Cart
    Cart "1" --> "*" CartItem : contains
    CartItem "*" --> "1" Dinner : references
    CartItem "*" --> "1" ServingStyle : uses
    CartItem "1" --> "*" CustomizationAction : has

    %% Relationships - Menu
    Dinner "1" --> "*" DinnerDish : includes
    DinnerDish "*" --> "1" Dish : references
    Dinner "*" --> "*" ServingStyle : supports
    Dish --> DishCategory : categorized

    %% Relationships - Order
    Order "1" --> "*" OrderItem : contains
    Order --> OrderStatus : has
    OrderItem "*" --> "1" Dinner : references
    OrderItem "*" --> "1" ServingStyle : uses
    OrderItem "1" --> "*" CustomizationAction : has

    %% Relationships - Customization
    CustomizationAction "*" --> "0..1" Dish : modifies
```

### 1.2 Entity Relationships (ER Style)

```mermaid
erDiagram
    CUSTOMERS ||--o| CARTS : has
    CUSTOMERS ||--o{ ORDERS : places

    CARTS ||--o{ CART_ITEMS : contains
    CART_ITEMS }o--|| DINNERS : references
    CART_ITEMS }o--|| SERVING_STYLES : uses
    CART_ITEMS ||--o{ CUSTOMIZATION_ACTIONS : has

    DINNERS ||--o{ DINNER_DISHES : includes
    DINNER_DISHES }o--|| DISHES : references
    DINNERS }o--o{ SERVING_STYLES : supports

    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDER_ITEMS }o--|| DINNERS : references
    ORDER_ITEMS }o--|| SERVING_STYLES : uses
    ORDER_ITEMS ||--o{ CUSTOMIZATION_ACTIONS : has

    CUSTOMIZATION_ACTIONS }o--o| DISHES : modifies

    CUSTOMERS {
        Long id PK
        String email UK
        String password
        String name
        String address
        String phone
        Role role
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    CARTS {
        Long id PK
        Long customer_id FK
    }

    CART_ITEMS {
        Long id PK
        Long cart_id FK
        Long dinner_id FK
        Long serving_style_id FK
        Integer quantity
    }

    DINNERS {
        Long id PK
        String name
        String description
        Integer base_price
        String image_url
    }

    DISHES {
        Long id PK
        String name
        String description
        Integer base_price
        Integer default_quantity
        Integer current_stock
        Integer minimum_stock
        DishCategory category
    }

    DINNER_DISHES {
        Long id PK
        Long dinner_id FK
        Long dish_id FK
        Integer quantity
    }

    SERVING_STYLES {
        Long id PK
        String name
        Integer additional_price
        String description
    }

    ORDERS {
        Long id PK
        Long customer_id FK
        LocalDateTime order_date
        LocalDateTime delivery_date
        LocalDateTime delivered_at
        String delivery_address
        OrderStatus status
        Integer total_price
        LocalDateTime created_at
        LocalDateTime updated_at
    }

    ORDER_ITEMS {
        Long id PK
        Long order_id FK
        Long dinner_id FK
        Long serving_style_id FK
        Integer quantity
        Integer price
    }

    CUSTOMIZATION_ACTIONS {
        Long id PK
        Long cart_item_id FK
        Long order_item_id FK
        String action
        Long dish_id FK
        Integer quantity
    }

    STAFF_AVAILABILITY {
        Long id PK
        Integer available_cooks
        Integer available_drivers
        Integer total_cooks
        Integer total_drivers
    }
```

---

## 2. Service Layer Class Diagram

```mermaid
classDiagram
    direction TB

    %% Auth Services
    class AuthService {
        -AuthRepository authRepository
        -PasswordEncoder passwordEncoder
        -JwtUtil jwtUtil
        -AuthenticationManager authenticationManager
        +register(RegisterRequest) void
        +login(LoginRequest) TokenResponse
        +refresh(RefreshTokenRequest) TokenResponse
    }

    class CustomUserDetailsService {
        <<Service>>
        -AuthRepository authRepository
        +loadUserByUsername(String) UserDetails
    }

    %% Cart Service
    class CartService {
        -CartRepository cartRepository
        -CartItemRepository cartItemRepository
        -MenuRepository menuRepository
        -ServingStyleRepository servingStyleRepository
        -DishRepository dishRepository
        -AuthRepository authRepository
        -getCurrentCustomer() Customer
        +getCart() CartResponse
        +addItemToCart(AddToCartRequest) void
        +updateItemQuantity(Long, UpdateQuantityRequest) void
        +updateItemOptions(Long, UpdateOptionsRequest) void
        +removeItem(Long) void
        +customizeCartItem(Long, CustomizeCartItemRequest) void
        -mapToCartItemResponse(CartItem) CartItemResponse
    }

    %% Menu Service
    class MenuService {
        -MenuRepository menuRepository
        +getMenuList() List~MenuListResponse~
        +getMenuDetails(Long) MenuDetailResponse
    }

    %% Order Service
    class OrderService {
        -OrderRepository orderRepository
        -CartRepository cartRepository
        -AuthRepository authRepository
        -MenuRepository menuRepository
        -getCurrentCustomer() Customer
        +checkout(CheckoutRequest) Long
        +getOrderHistory() List~OrderHistoryResponse~
        +getOrderDetails(Long) OrderDetailResponse
    }

    %% Staff Service
    class StaffService {
        -OrderRepository orderRepository
        -DishRepository dishRepository
        -MenuRepository menuRepository
        -StaffAvailabilityRepository staffAvailabilityRepository
        +getActiveOrders() List~ActiveOrdersResponse~
        +getStaffAvailability() StaffAvailabilityResponse
        +updateOrderStatus(Long, UpdateOrderStatusRequest) void
        +driverReturn(Long) void
        -deductInventoryForOrder(Order) void
        +getInventory() List~InventoryItemResponse~
        +checkOrderInventory(Long) OrderInventoryCheckResponse
    }

    class InventoryScheduler {
        -DishRepository dishRepository
        +refillGeneralInventory() void
        +refillLiquorInventory() void
    }

    %% Utility Classes
    class JwtUtil {
        -String secret
        -long accessTokenValidity
        -long refreshTokenValidity
        -getSigningKey() Key
        +generateAccessToken(String, String) String
        +generateRefreshToken(String, String) String
        +validateToken(String) boolean
        +getEmailFromToken(String) String
        +getRoleFromToken(String) String
        +isTokenExpired(String) boolean
        +getAccessTokenValidity() long
    }

    %% Dependencies
    AuthService ..> JwtUtil : uses
    AuthService ..> AuthRepository : uses
    CustomUserDetailsService ..> AuthRepository : uses

    CartService ..> CartRepository : uses
    CartService ..> CartItemRepository : uses
    CartService ..> MenuRepository : uses
    CartService ..> DishRepository : uses
    CartService ..> AuthRepository : uses

    MenuService ..> MenuRepository : uses

    OrderService ..> OrderRepository : uses
    OrderService ..> CartRepository : uses
    OrderService ..> AuthRepository : uses
    OrderService ..> MenuRepository : uses

    StaffService ..> OrderRepository : uses
    StaffService ..> DishRepository : uses
    StaffService ..> MenuRepository : uses
    StaffService ..> StaffAvailabilityRepository : uses

    InventoryScheduler ..> DishRepository : uses
```

---

## 3. Controller Layer Class Diagram

```mermaid
classDiagram
    direction TB

    class AuthController {
        <<RestController>>
        -AuthService authService
        +register(RegisterRequest) ResponseEntity~ApiResponse~
        +login(LoginRequest) ResponseEntity~TokenResponse~
        +refresh(RefreshTokenRequest) ResponseEntity~TokenResponse~
        +logout() ResponseEntity~ApiResponse~
    }

    class CartController {
        <<RestController>>
        -CartService cartService
        +getCart() ResponseEntity~CartResponse~
        +addItemToCart(AddToCartRequest) ResponseEntity~CartResponse~
        +updateItemQuantity(Long, UpdateQuantityRequest) ResponseEntity~CartResponse~
        +updateItemOptions(Long, UpdateOptionsRequest) ResponseEntity~CartResponse~
        +removeItem(Long) ResponseEntity~CartResponse~
        +customizeCartItem(Long, CustomizeCartItemRequest) ResponseEntity~CartResponse~
    }

    class MenuController {
        <<RestController>>
        -MenuService menuService
        +getMenuList() ResponseEntity~List~
        +getMenuDetails(Long) ResponseEntity~MenuDetailResponse~
    }

    class OrderController {
        <<RestController>>
        -OrderService orderService
        +checkout(CheckoutRequest) ResponseEntity~ApiResponse~
        +getOrderHistory() ResponseEntity~List~
        +getOrderDetails(Long) ResponseEntity~OrderDetailResponse~
    }

    class StaffController {
        <<RestController>>
        -StaffService staffService
        +getActiveOrders() ResponseEntity~List~
        +updateOrderStatus(Long, UpdateOrderStatusRequest) ResponseEntity~ApiResponse~
        +getInventory() ResponseEntity~List~
        +checkOrderInventory(Long) ResponseEntity~OrderInventoryCheckResponse~
        +getStaffAvailability() ResponseEntity~StaffAvailabilityResponse~
        +driverReturn(Long) ResponseEntity~ApiResponse~
    }

    %% API Endpoints
    AuthController : POST /api/auth/register
    AuthController : POST /api/auth/login
    AuthController : POST /api/auth/refresh
    AuthController : POST /api/auth/logout

    CartController : GET /api/cart
    CartController : POST /api/cart/items
    CartController : PUT /api/cart/items/{id}/quantity
    CartController : PUT /api/cart/items/{id}/options
    CartController : DELETE /api/cart/items/{id}
    CartController : POST /api/cart/items/{id}/customize

    MenuController : GET /api/menu/list
    MenuController : GET /api/menu/details/{id}

    OrderController : POST /api/orders/checkout
    OrderController : GET /api/orders/history
    OrderController : GET /api/orders/{id}

    StaffController : GET /api/staff/orders/active
    StaffController : PUT /api/staff/orders/{id}/status
    StaffController : GET /api/staff/inventory
    StaffController : GET /api/staff/orders/{id}/inventory-check
    StaffController : GET /api/staff/availability
    StaffController : POST /api/staff/orders/{id}/driver-return

    %% Dependencies
    AuthController ..> AuthService : uses
    CartController ..> CartService : uses
    MenuController ..> MenuService : uses
    OrderController ..> OrderService : uses
    StaffController ..> StaffService : uses
```

---

## 4. DTO Class Diagram

### 4.1 Auth DTOs

```mermaid
classDiagram
    direction LR

    class LoginRequest {
        <<DTO>>
        -String email
        -String password
    }

    class RegisterRequest {
        <<DTO>>
        -String email
        -String password
        -String name
        -String address
        -String phone
    }

    class RefreshTokenRequest {
        <<DTO>>
        -String refreshToken
    }

    class TokenResponse {
        <<DTO>>
        -String accessToken
        -String refreshToken
        -String tokenType
        -long expiresIn
    }
```

### 4.2 Cart DTOs

```mermaid
classDiagram
    direction TB

    class AddToCartRequest {
        <<DTO>>
        -Long dinnerId
        -Long servingStyleId
        -Integer quantity
        -List~CustomizationRequest~ customizations
    }

    class CustomizationRequest {
        <<DTO>>
        -String action
        -Long dishId
        -Integer quantity
    }

    class UpdateQuantityRequest {
        <<DTO>>
        -Integer quantity
    }

    class UpdateOptionsRequest {
        <<DTO>>
        -Long servingStyleId
        -List~CustomizationRequest~ customizations
    }

    class CustomizeCartItemRequest {
        <<DTO>>
        -String action
        -Long dishId
        -Integer quantity
    }

    class CartResponse {
        <<DTO>>
        -Long cartId
        -List~CartItemResponse~ items
        -Integer totalPrice
    }

    class CartItemResponse {
        <<DTO>>
        -Long itemId
        -Long dinnerId
        -String dinnerName
        -Integer dinnerBasePrice
        -Long servingStyleId
        -String servingStyleName
        -Integer servingStylePrice
        -Integer quantity
        -Integer itemTotalPrice
        -List~CustomizationResponse~ customizations
    }

    class CustomizationResponse {
        <<DTO>>
        -String action
        -Long dishId
        -String dishName
        -Integer quantity
        -Integer pricePerUnit
    }

    AddToCartRequest *-- CustomizationRequest : contains
    UpdateOptionsRequest *-- CustomizationRequest : contains
    CartResponse *-- CartItemResponse : contains
    CartItemResponse *-- CustomizationResponse : contains
```

### 4.3 Menu DTOs

```mermaid
classDiagram
    direction TB

    class MenuListResponse {
        <<DTO>>
        -Long id
        -String name
        -String description
        -Integer basePrice
        -String imageUrl
    }

    class MenuDetailResponse {
        <<DTO>>
        -Long id
        -String name
        -String description
        -Integer basePrice
        -String imageUrl
        -List~DishInfo~ dishes
        -List~ServingStyleInfo~ availableStyles
    }

    class DishInfo {
        <<DTO>>
        -Long id
        -String name
        -String description
        -Integer basePrice
        -Integer quantity
    }

    class ServingStyleInfo {
        <<DTO>>
        -Long id
        -String name
        -Integer additionalPrice
        -String description
    }

    MenuDetailResponse *-- DishInfo : contains
    MenuDetailResponse *-- ServingStyleInfo : contains
```

### 4.4 Order DTOs

```mermaid
classDiagram
    direction TB

    class CheckoutRequest {
        <<DTO>>
        -LocalDateTime deliveryDate
        -String deliveryAddress
    }

    class OrderHistoryResponse {
        <<DTO>>
        -Long orderId
        -LocalDateTime orderDate
        -LocalDateTime deliveryDate
        -LocalDateTime deliveredAt
        -String deliveryAddress
        -OrderStatus status
        -Integer totalPrice
        -int itemCount
    }

    class OrderDetailResponse {
        <<DTO>>
        -Long orderId
        -LocalDateTime orderDate
        -LocalDateTime deliveryDate
        -LocalDateTime deliveredAt
        -String deliveryAddress
        -OrderStatus status
        -Integer totalPrice
        -List~OrderItemInfo~ items
    }

    class OrderItemInfo {
        <<DTO>>
        -Long itemId
        -Long dinnerId
        -String dinnerName
        -String servingStyleName
        -Integer quantity
        -Integer price
        -List~BaseDishInfo~ baseDishes
        -List~CustomizationInfo~ customizations
    }

    class BaseDishInfo {
        <<DTO>>
        -Long dishId
        -String dishName
        -Integer quantity
    }

    class CustomizationInfo {
        <<DTO>>
        -String action
        -Long dishId
        -String dishName
        -Integer quantity
        -Integer pricePerUnit
    }

    OrderDetailResponse *-- OrderItemInfo : contains
    OrderItemInfo *-- BaseDishInfo : contains
    OrderItemInfo *-- CustomizationInfo : contains
```

### 4.5 Staff DTOs

```mermaid
classDiagram
    direction TB

    class ActiveOrdersResponse {
        <<DTO>>
        -Long orderId
        -String customerName
        -String customerEmail
        -LocalDateTime orderDate
        -LocalDateTime deliveryDate
        -LocalDateTime deliveredAt
        -String deliveryAddress
        -OrderStatus status
        -Integer totalPrice
        -int itemCount
    }

    class UpdateOrderStatusRequest {
        <<DTO>>
        -OrderStatus status
    }

    class InventoryItemResponse {
        <<DTO>>
        -Long dishId
        -String dishName
        -Integer currentStock
        -Integer minimumStock
    }

    class StaffAvailabilityResponse {
        <<DTO>>
        -Integer availableCooks
        -Integer totalCooks
        -Integer availableDrivers
        -Integer totalDrivers
        -Boolean canStartCooking
        -Boolean canStartDelivery
    }

    class OrderInventoryCheckResponse {
        <<DTO>>
        -Long orderId
        -String customerName
        -String customerEmail
        -Boolean isSufficient
        -List~DishRequirement~ requiredItems
    }

    class DishRequirement {
        <<DTO>>
        -Long dishId
        -String dishName
        -Integer requiredQuantity
        -Integer currentStock
        -Boolean isSufficient
        -Integer shortage
    }

    OrderInventoryCheckResponse *-- DishRequirement : contains
```

---

## 5. Repository Layer Class Diagram

```mermaid
classDiagram
    direction TB

    class JpaRepository~T, ID~ {
        <<interface>>
        +save(T) T
        +findById(ID) Optional~T~
        +findAll() List~T~
        +delete(T) void
        +existsById(ID) boolean
    }

    class AuthRepository {
        <<interface>>
        +findByEmail(String) Optional~Customer~
        +existsByEmail(String) boolean
    }

    class CartRepository {
        <<interface>>
        +findByCustomerId(Long) Optional~Cart~
        +findByCustomerIdWithItems(Long) Optional~Cart~
    }

    class CartItemRepository {
        <<interface>>
        +findByIdWithCustomizations(Long) Optional~CartItem~
    }

    class MenuRepository {
        <<interface>>
        +findByIdWithDishes(Long) Optional~Dinner~
        +findByIdWithStyles(Long) Optional~Dinner~
    }

    class DishRepository {
        <<interface>>
    }

    class ServingStyleRepository {
        <<interface>>
    }

    class OrderRepository {
        <<interface>>
        +findByCustomerIdOrderByOrderDateDesc(Long) List~Order~
        +findByIdWithDetails(Long) Optional~Order~
        +findActiveOrders(List~OrderStatus~) List~Order~
    }

    class OrderItemRepository {
        <<interface>>
    }

    class StaffAvailabilityRepository {
        <<interface>>
        +getStaffAvailability() StaffAvailability
    }

    AuthRepository --|> JpaRepository : extends
    CartRepository --|> JpaRepository : extends
    CartItemRepository --|> JpaRepository : extends
    MenuRepository --|> JpaRepository : extends
    DishRepository --|> JpaRepository : extends
    ServingStyleRepository --|> JpaRepository : extends
    OrderRepository --|> JpaRepository : extends
    OrderItemRepository --|> JpaRepository : extends
    StaffAvailabilityRepository --|> JpaRepository : extends
```

---

## 6. Configuration & Security Class Diagram

```mermaid
classDiagram
    direction TB

    class SecurityConfig {
        <<Configuration>>
        -JwtAuthenticationFilter jwtAuthenticationFilter
        +filterChain(HttpSecurity) SecurityFilterChain
        +passwordEncoder() PasswordEncoder
        +authenticationManager(AuthenticationConfiguration) AuthenticationManager
        +corsConfigurationSource() CorsConfigurationSource
    }

    class JwtAuthenticationFilter {
        <<Component>>
        -JwtUtil jwtUtil
        -UserDetailsService userDetailsService
        #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain) void
        -extractTokenFromRequest(HttpServletRequest) String
    }

    class OncePerRequestFilter {
        <<abstract>>
        #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain) void
    }

    JwtAuthenticationFilter --|> OncePerRequestFilter : extends
    SecurityConfig ..> JwtAuthenticationFilter : uses
    JwtAuthenticationFilter ..> JwtUtil : uses
```

---

## 7. Exception Handling Class Diagram

```mermaid
classDiagram
    direction TB

    class CustomException {
        <<RuntimeException>>
        -String message
        +CustomException(String)
        +getMessage() String
    }

    class GlobalExceptionHandler {
        <<ControllerAdvice>>
        +handleCustomException(CustomException) ResponseEntity~ErrorResponse~
        +handleValidationException(MethodArgumentNotValidException) ResponseEntity~ErrorResponse~
        +handleGenericException(Exception) ResponseEntity~ErrorResponse~
    }

    class ErrorResponse {
        <<DTO>>
        -int status
        -String error
        -String message
        -LocalDateTime timestamp
    }

    class ApiResponse~T~ {
        <<DTO>>
        -boolean success
        -String message
        -T data
        +success(String) ApiResponse
        +success(String, T) ApiResponse
        +error(String) ApiResponse
    }

    RuntimeException <|-- CustomException : extends
    GlobalExceptionHandler ..> ErrorResponse : creates
    GlobalExceptionHandler ..> CustomException : handles
```

---

## 8. Complete System Overview

```mermaid
classDiagram
    direction TB

    %% Presentation Layer
    class Controllers {
        <<layer>>
        AuthController
        CartController
        MenuController
        OrderController
        StaffController
    }

    %% Business Layer
    class Services {
        <<layer>>
        AuthService
        CartService
        MenuService
        OrderService
        StaffService
    }

    %% Data Access Layer
    class Repositories {
        <<layer>>
        AuthRepository
        CartRepository
        MenuRepository
        OrderRepository
        StaffAvailabilityRepository
    }

    %% Domain Layer
    class Entities {
        <<layer>>
        Customer
        Cart + CartItem
        Dinner + Dish + ServingStyle
        Order + OrderItem
        StaffAvailability
    }

    %% Infrastructure
    class Security {
        <<layer>>
        SecurityConfig
        JwtAuthenticationFilter
        JwtUtil
    }

    %% Data Transfer
    class DTOs {
        <<layer>>
        Request DTOs
        Response DTOs
    }

    Controllers --> Services : delegates to
    Services --> Repositories : uses
    Repositories --> Entities : manages
    Controllers --> DTOs : uses
    Services --> DTOs : uses
    Security --> Services : secures
    Controllers --> Security : protected by
```
