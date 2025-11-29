# DdoganZip System Sequence Diagrams

## 1. Register

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Register Request (email, password, name, address, phone)
    System-->>User: Registration Response (success/failure message)
```

## 2. Login

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Login Request (email, password)
    System-->>User: Auth Tokens (Access Token, Refresh Token)
```

## 3. View Menu

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Get Menu List Request
    System-->>User: Dinner List Response

    User->>System: Get Menu Details Request (dinnerId)
    System-->>User: Dinner Details Response (dishes, serving styles, price)
```

## 4. Manage Cart

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Get Cart Request
    System-->>User: Cart Items Response

    User->>System: Add Item Request (dinnerId, servingStyleId, quantity)
    System-->>User: Item Added Response

    User->>System: Update Quantity Request (itemId, newQuantity)
    System-->>User: Quantity Updated Response

    User->>System: Update Options Request (itemId, servingStyleId)
    System-->>User: Options Updated Response

    User->>System: Customize Item Request (itemId, ADD/REMOVE dish)
    System-->>User: Customization Applied Response

    User->>System: Remove Item Request (itemId)
    System-->>User: Item Removed Response
```

## 5. Checkout

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Checkout Request (deliveryAddress, deliveryDate)
    System-->>User: Order Created Response (orderId, orderDetails)
```

## 6. View Order

### 6-1. User - Order History

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: Get Order History Request
    System-->>User: Order List Response

    User->>System: Get Order Details Request (orderId)
    System-->>User: Order Details Response (status, items, price)
```

### 6-2. Staff - Active Orders

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: Get Active Orders Request
    System-->>Staff: Active Orders List Response (excludes DRIVER_RETURNED)
```

## 7. Manage Status (Staff)

### 7-1. Update Order Status

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: Update Status Request (orderId, newStatus)
    System-->>Staff: Status Updated Response
```

### 7-2. Check Inventory

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: Get Inventory Request
    System-->>Staff: Inventory List Response

    Staff->>System: Check Order Inventory Request (orderId)
    System-->>Staff: Inventory Check Response (canFulfill, shortages)
```

### 7-3. Driver Return

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: Driver Return Request (orderId)
    System-->>Staff: Driver Returned Response (driver now available)
```

### 7-4. Staff Availability

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: Get Staff Availability Request
    System-->>Staff: Availability Response (cooks, drivers count)
```
