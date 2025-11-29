# Package Diagram

This document contains the Mermaid package diagram for the DDogan Zip backend system.

## System Package Structure

```mermaid
flowchart TB
    subgraph com.ddoganzip["com.ddoganzip"]

        subgraph auth["auth"]
            auth_controller["controller"]
            auth_service["service"]
            auth_entity["entity"]
            auth_repository["repository"]
            auth_dto["dto"]
            auth_util["util"]
        end

        subgraph common["common"]
            common_classes["ApiResponse<br/>BaseEntity"]
        end

        subgraph config["config"]
            config_classes["SecurityConfig<br/>JwtAuthenticationFilter"]
        end

        subgraph customers["customers"]
            subgraph cart["cart"]
                cart_controller["controller"]
                cart_service["service"]
                cart_entity["entity"]
                cart_repository["repository"]
                cart_dto["dto"]
            end

            subgraph menu["menu"]
                menu_controller["controller"]
                menu_service["service"]
                menu_entity["entity"]
                menu_repository["repository"]
                menu_dto["dto"]
            end

            subgraph orders["orders"]
                orders_controller["controller"]
                orders_service["service"]
                orders_entity["entity"]
                orders_repository["repository"]
                orders_dto["dto"]
            end
        end

        subgraph staff["staff"]
            staff_controller["controller"]
            staff_service["service"]
            staff_entity["entity"]
            staff_repository["repository"]
            staff_dto["dto"]
        end

        subgraph exception["exception"]
            exception_classes["CustomException<br/>GlobalExceptionHandler<br/>ErrorResponse"]
        end

        subgraph util["util"]
            util_classes["PasswordHashGenerator"]
        end
    end

    %% Dependencies between packages
    config --> auth_service
    config --> auth_util

    auth_controller --> auth_service
    auth_service --> auth_repository
    auth_service --> auth_util
    auth_service --> cart_entity

    cart_controller --> cart_service
    cart_service --> cart_repository
    cart_service --> auth_repository
    cart_service --> menu_repository
    cart_service --> orders_entity

    menu_controller --> menu_service
    menu_service --> menu_repository

    orders_controller --> orders_service
    orders_service --> orders_repository
    orders_service --> auth_repository
    orders_service --> cart_repository
    orders_service --> menu_repository

    staff_controller --> staff_service
    staff_service --> orders_repository
    staff_service --> staff_repository
    staff_service --> cart_repository
    staff_service --> menu_repository

    auth_entity --> common
    orders_entity --> common
```

## Package Dependency Overview

```mermaid
flowchart LR
    subgraph Presentation["Presentation Layer"]
        Controllers["Controllers"]
    end

    subgraph Business["Business Layer"]
        Services["Services"]
    end

    subgraph Persistence["Persistence Layer"]
        Repositories["Repositories"]
    end

    subgraph Domain["Domain Layer"]
        Entities["Entities"]
        DTOs["DTOs"]
    end

    subgraph Infrastructure["Infrastructure"]
        Config["Config"]
        Security["Security"]
        Exception["Exception Handling"]
    end

    Controllers --> Services
    Services --> Repositories
    Services --> Entities
    Controllers --> DTOs
    Services --> DTOs
    Repositories --> Entities

    Config --> Security
    Security --> Services
    Controllers -.-> Exception
    Services -.-> Exception
```

## Module Dependencies

```mermaid
flowchart TB
    subgraph External["External Clients"]
        Web["Web Frontend"]
        Mobile["Mobile App"]
    end

    subgraph API["REST API Layer"]
        AuthAPI["/api/auth"]
        MenuAPI["/api/menu"]
        CartAPI["/api/cart"]
        OrdersAPI["/api/orders"]
        StaffAPI["/api/staff"]
    end

    subgraph Core["Core Modules"]
        AuthModule["Auth Module"]
        MenuModule["Menu Module"]
        CartModule["Cart Module"]
        OrdersModule["Orders Module"]
        StaffModule["Staff Module"]
    end

    subgraph Data["Data Layer"]
        CustomerDB[(customers)]
        MenuDB[(dinners, dishes)]
        CartDB[(carts, cart_items)]
        OrderDB[(orders, order_items)]
        StaffDB[(staff_availability)]
    end

    Web --> API
    Mobile --> API

    AuthAPI --> AuthModule
    MenuAPI --> MenuModule
    CartAPI --> CartModule
    OrdersAPI --> OrdersModule
    StaffAPI --> StaffModule

    AuthModule --> CustomerDB
    MenuModule --> MenuDB
    CartModule --> CartDB
    CartModule --> CustomerDB
    CartModule --> MenuDB
    OrdersModule --> OrderDB
    OrdersModule --> CustomerDB
    OrdersModule --> CartDB
    StaffModule --> OrderDB
    StaffModule --> MenuDB
    StaffModule --> StaffDB
```

## Package Details

| Package | Description | Key Classes |
|---------|-------------|-------------|
| `auth` | Authentication & Authorization | AuthController, AuthService, Customer, JwtUtil |
| `common` | Shared base classes | ApiResponse, BaseEntity |
| `config` | Security & application configuration | SecurityConfig, JwtAuthenticationFilter |
| `customers.cart` | Shopping cart management | CartController, CartService, Cart, CartItem |
| `customers.menu` | Menu browsing | MenuController, MenuService, Dinner, Dish |
| `customers.orders` | Order processing | OrderController, OrderService, Order, OrderItem |
| `staff` | Staff operations & inventory | StaffController, StaffService, StaffAvailability |
| `exception` | Global exception handling | CustomException, GlobalExceptionHandler |
