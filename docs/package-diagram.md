# Package Diagram

This document contains the Mermaid package diagram for the DDogan Zip backend system.

## Package Hierarchy

```mermaid
graph TB
    subgraph com.ddoganzip

        subgraph auth[auth]
            auth_c[controller]
            auth_s[service]
            auth_e[entity]
            auth_r[repository]
            auth_d[dto]
            auth_u[util]
        end

        subgraph customers[customers]
            subgraph cart[cart]
                cart_c[controller]
                cart_s[service]
                cart_e[entity]
                cart_r[repository]
                cart_d[dto]
            end

            subgraph menu[menu]
                menu_c[controller]
                menu_s[service]
                menu_e[entity]
                menu_r[repository]
                menu_d[dto]
            end

            subgraph orders[orders]
                orders_c[controller]
                orders_s[service]
                orders_e[entity]
                orders_r[repository]
                orders_d[dto]
            end
        end

        subgraph staff[staff]
            staff_c[controller]
            staff_s[service]
            staff_e[entity]
            staff_r[repository]
            staff_d[dto]
        end

        common[common]
        config[config]
        exception[exception]
    end
```

## Layered Architecture

```mermaid
graph TB
    subgraph Presentation["Presentation Layer"]
        Controllers[Controllers]
    end

    subgraph Business["Business Layer"]
        Services[Services]
    end

    subgraph Persistence["Persistence Layer"]
        Repositories[Repositories]
    end

    subgraph Domain["Domain Layer"]
        Entities[Entities]
    end

    Controllers --> Services
    Services --> Repositories
    Repositories --> Entities
    Services --> Entities
```

## Package Summary

| Package | Description |
|---------|-------------|
| `auth` | Authentication & Authorization |
| `customers.cart` | Shopping cart management |
| `customers.menu` | Menu browsing |
| `customers.orders` | Order processing |
| `staff` | Staff operations & inventory |
| `common` | Shared base classes |
| `config` | Security configuration |
| `exception` | Exception handling |
