# DdoganZip Backend API Documentation

## 목차
1. [인증 API](#1-인증-api)
2. [메뉴 API](#2-메뉴-api)
3. [장바구니 API](#3-장바구니-api)
4. [주문 API](#4-주문-api)
5. [스태프 API](#5-스태프-api)
6. [공통 응답 형식](#6-공통-응답-형식)
7. [에러 코드](#7-에러-코드)

---

## 1. 인증 API

### 1.1 회원가입

**엔드포인트:** `POST /api/auth/register`

**인증 필요:** 없음

**설명:** 새로운 고객 계정을 생성합니다.

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| email | String | ✅ | 이메일 주소 | 유효한 이메일 형식, 중복 불가 |
| password | String | ✅ | 비밀번호 | 최소 8자 이상 |
| name | String | ✅ | 사용자 이름 | 1자 이상 |
| phoneNumber | String | ✅ | 전화번호 | 유효한 전화번호 형식 |
| address | String | ✅ | 배송 주소 | 1자 이상 |

**Request 예시:**
```json
{
  "email": "customer@example.com",
  "password": "password123",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "address": "서울시 강남구 테헤란로 123"
}
```

**Response (200 OK):**

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 (true) |
| message | String | 성공 메시지 |
| data | null | 데이터 없음 |

**Response 예시:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": null
}
```

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 입력값
- `409 Conflict`: 이미 존재하는 이메일

---

### 1.2 로그인

**엔드포인트:** `POST /api/auth/login`

**인증 필요:** 없음

**설명:** 이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| email | String | ✅ | 이메일 주소 | 등록된 이메일 |
| password | String | ✅ | 비밀번호 | 등록된 비밀번호 |

**Request 예시:**
```json
{
  "email": "customer@example.com",
  "password": "password123"
}
```

**Response (200 OK):**

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 (true) |
| message | String | 성공 메시지 |
| data | TokenResponse | 토큰 정보 |

**TokenResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| accessToken | String | 액세스 토큰 (1시간 유효) |
| refreshToken | String | 리프레시 토큰 (7일 유효) |
| tokenType | String | 토큰 타입 ("Bearer") |

**Response 예시:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer"
  }
}
```

**에러 응답:**
- `401 Unauthorized`: 잘못된 이메일 또는 비밀번호

---

### 1.3 토큰 갱신

**엔드포인트:** `POST /api/auth/refresh`

**인증 필요:** 없음 (리프레시 토큰 필요)

**설명:** 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| refreshToken | String | ✅ | 리프레시 토큰 | 유효한 리프레시 토큰 |

**Request 예시:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**

TokenResponse와 동일 (새로운 액세스 토큰과 리프레시 토큰 포함)

**에러 응답:**
- `401 Unauthorized`: 유효하지 않거나 만료된 리프레시 토큰

---

## 2. 메뉴 API

### 2.1 메뉴 목록 조회

**엔드포인트:** `GET /api/menu/list`

**인증 필요:** 없음

**설명:** 사용 가능한 모든 디너 메뉴 목록을 조회합니다.

**Request Parameters:** 없음

**Response (200 OK):**

Array of MenuListResponse

**MenuListResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| dinnerId | Long | 디너 ID |
| name | String | 디너 이름 |
| description | String | 디너 설명 |
| basePrice | Integer | 기본 가격 (원 단위) |
| imageUrl | String | 이미지 URL |

**Response 예시:**
```json
[
  {
    "dinnerId": 1,
    "name": "Valentine Dinner",
    "description": "작은 하트 모양과 큐피드가 장식된 접시에 냅킨과 함께 와인과 스테이크가 제공",
    "basePrice": 45000,
    "imageUrl": "https://example.com/valentine.jpg"
  },
  {
    "dinnerId": 2,
    "name": "French Dinner",
    "description": "프렌치 스타일의 우아한 디너",
    "basePrice": 60000,
    "imageUrl": "https://example.com/french.jpg"
  }
]
```

---

### 2.2 메뉴 상세 조회

**엔드포인트:** `GET /api/menu/details/{dinnerId}`

**인증 필요:** 없음

**설명:** 특정 디너의 상세 정보를 조회합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| dinnerId | Long | ✅ | 디너 ID |

**Response (200 OK):**

**MenuDetailResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| dinnerId | Long | 디너 ID |
| name | String | 디너 이름 |
| description | String | 디너 설명 |
| basePrice | Integer | 기본 가격 (원) |
| imageUrl | String | 이미지 URL |
| dishes | Array | 포함된 요리 목록 |
| availableStyles | Array | 선택 가능한 서빙 스타일 |

**Dish:**

| 필드 | 타입 | 설명 |
|------|------|------|
| dishId | Long | 요리 ID |
| name | String | 요리 이름 |
| description | String | 요리 설명 |

**ServingStyle:**

| 필드 | 타입 | 설명 |
|------|------|------|
| styleId | Long | 서빙 스타일 ID |
| name | String | 스타일 이름 |
| additionalPrice | Integer | 추가 가격 (원) |
| description | String | 스타일 설명 |

**Response 예시:**
```json
{
  "dinnerId": 1,
  "name": "Valentine Dinner",
  "description": "작은 하트 모양과 큐피드가 장식된 접시에 냅킨과 함께 와인과 스테이크가 제공",
  "basePrice": 45000,
  "imageUrl": "https://example.com/valentine.jpg",
  "dishes": [
    {
      "dishId": 1,
      "name": "Steak",
      "description": "프리미엄 스테이크"
    },
    {
      "dishId": 2,
      "name": "Wine",
      "description": "레드 와인 1잔"
    }
  ],
  "availableStyles": [
    {
      "styleId": 1,
      "name": "Simple",
      "additionalPrice": 0,
      "description": "플라스틱 접시와 플라스틱 컵, 종이 냅킨이 플라스틱 쟁반에 제공"
    },
    {
      "styleId": 2,
      "name": "Grand",
      "additionalPrice": 15000,
      "description": "도자기 접시와 도자기 컵, 흰색 면 냅킨이 나무 쟁반에 제공"
    },
    {
      "styleId": 3,
      "name": "Deluxe",
      "additionalPrice": 30000,
      "description": "꽃병, 도자기 접시와 도자기 컵, 린넨 냅킨이 나무 쟁반에 제공"
    }
  ]
}
```

**비즈니스 규칙:**
- Champagne Feast Dinner (ID: 4)는 Simple 스타일을 선택할 수 없으며, Grand 또는 Deluxe만 선택 가능

**에러 응답:**
- `404 Not Found`: 존재하지 않는 디너 ID

---

## 3. 장바구니 API

### 3.1 장바구니 조회

**엔드포인트:** `GET /api/cart`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 현재 로그인한 사용자의 장바구니를 조회합니다.

**Request Parameters:** 없음

**Response (200 OK):**

**CartResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| cartId | Long | 장바구니 ID |
| items | Array | 장바구니 아이템 목록 |
| totalPrice | Integer | 전체 합계 금액 (원) |

**CartItemResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| itemId | Long | 장바구니 아이템 ID |
| dinnerId | Long | 디너 ID |
| dinnerName | String | 디너 이름 |
| dinnerBasePrice | Integer | 디너 기본 가격 (원) |
| servingStyleId | Long | 서빙 스타일 ID |
| servingStyleName | String | 서빙 스타일 이름 |
| servingStylePrice | Integer | 서빙 스타일 추가 가격 (원) |
| quantity | Integer | 수량 |
| itemTotalPrice | Integer | 아이템 총 가격 = (기본가격 + 서빙추가) × 수량 (원) |
| customizations | Array | 커스터마이징 목록 |

**CustomizationResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| action | String | 액션 타입 ("ADD", "REMOVE", "REPLACE") |
| dishId | Long | 요리 ID |
| dishName | String | 요리 이름 |
| quantity | Integer | 수량 |

**Response 예시:**
```json
{
  "cartId": 1,
  "items": [
    {
      "itemId": 1,
      "dinnerId": 1,
      "dinnerName": "Valentine Dinner",
      "dinnerBasePrice": 45000,
      "servingStyleId": 3,
      "servingStyleName": "Deluxe",
      "servingStylePrice": 30000,
      "quantity": 2,
      "itemTotalPrice": 150000,
      "customizations": [
        {
          "action": "ADD",
          "dishId": 8,
          "dishName": "Champagne",
          "quantity": 1
        },
        {
          "action": "REMOVE",
          "dishId": 2,
          "dishName": "Wine",
          "quantity": 0
        }
      ]
    }
  ],
  "totalPrice": 150000
}
```

**에러 응답:**
- `401 Unauthorized`: 인증되지 않은 요청
- `404 Not Found`: 장바구니가 존재하지 않음

---

### 3.2 장바구니에 아이템 추가

**엔드포인트:** `POST /api/cart/items`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니에 새로운 디너 아이템을 추가합니다.

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| dinnerId | Long | ✅ | 디너 ID | 존재하는 디너 ID |
| servingStyleId | Long | ✅ | 서빙 스타일 ID | 해당 디너에서 선택 가능한 스타일 |
| quantity | Integer | ✅ | 수량 | 1 이상 |
| customizations | Array | ❌ | 커스터마이징 목록 | 선택 사항 |

**CustomizationRequest:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| action | String | ✅ | 액션 타입 | "ADD", "REMOVE", "REPLACE" 중 하나 |
| dishId | Long | ✅ | 요리 ID | 존재하는 요리 ID |
| quantity | Integer | ✅ | 수량 | ADD/REPLACE: 1 이상, REMOVE: 무시됨 |

**Request 예시:**
```json
{
  "dinnerId": 1,
  "servingStyleId": 3,
  "quantity": 2,
  "customizations": [
    {
      "action": "ADD",
      "dishId": 8,
      "quantity": 1
    },
    {
      "action": "REMOVE",
      "dishId": 2,
      "quantity": 0
    }
  ]
}
```

**Response (200 OK):**

전체 CartResponse 반환

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 입력값
- `401 Unauthorized`: 인증되지 않은 요청
- `404 Not Found`: 디너 또는 서빙 스타일이 존재하지 않음

---

### 3.3 장바구니 아이템 수량 변경

**엔드포인트:** `PUT /api/cart/items/{itemId}/quantity`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니 아이템의 수량을 변경합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| itemId | Long | ✅ | 장바구니 아이템 ID |

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| quantity | Integer | ✅ | 새로운 수량 | 1 이상 |

**Request 예시:**
```json
{
  "quantity": 3
}
```

**Response (200 OK):**

전체 CartResponse 반환

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 수량
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: 다른 사용자의 장바구니 아이템
- `404 Not Found`: 아이템이 존재하지 않음

---

### 3.4 장바구니 아이템 옵션 변경

**엔드포인트:** `PUT /api/cart/items/{itemId}/options`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니 아이템의 서빙 스타일과 커스터마이징을 변경합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| itemId | Long | ✅ | 장바구니 아이템 ID |

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| servingStyleId | Long | ✅ | 새로운 서빙 스타일 ID | 해당 디너에서 선택 가능한 스타일 |
| customizations | Array | ❌ | 새로운 커스터마이징 목록 | 기존 커스터마이징은 모두 교체됨 |

**Request 예시:**
```json
{
  "servingStyleId": 2,
  "customizations": [
    {
      "action": "ADD",
      "dishId": 5,
      "quantity": 2
    }
  ]
}
```

**Response (200 OK):**

전체 CartResponse 반환

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 입력값
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: 다른 사용자의 장바구니 아이템
- `404 Not Found`: 아이템 또는 서빙 스타일이 존재하지 않음

---

### 3.5 장바구니 아이템 제거

**엔드포인트:** `DELETE /api/cart/items/{itemId}`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니에서 아이템을 제거합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| itemId | Long | ✅ | 장바구니 아이템 ID |

**Request Body:** 없음

**Response (200 OK):**

전체 CartResponse 반환

**에러 응답:**
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: 다른 사용자의 장바구니 아이템
- `404 Not Found`: 아이템이 존재하지 않음

---

### 3.6 장바구니 아이템 커스터마이징

**엔드포인트:** `POST /api/cart/items/{itemId}/customize`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니 아이템에 개별 요리를 추가/제거/변경합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| itemId | Long | ✅ | 장바구니 아이템 ID |

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| action | String | ✅ | 액션 타입 | "ADD", "REMOVE", "REPLACE" 중 하나 |
| dishId | Long | ✅ | 요리 ID | 존재하는 요리 ID |
| quantity | Integer | ✅ | 수량 | ADD/REPLACE: 1 이상, REMOVE: 무시됨 |

**Action 설명:**
- **ADD**: 디너에 새로운 요리를 추가하거나 기존 커스터마이징 업데이트
- **REMOVE**: 디너에서 특정 요리 커스터마이징을 제거
- **REPLACE**: 기존 요리의 수량을 변경

**Request 예시 - Steak 추가:**
```json
{
  "action": "ADD",
  "dishId": 1,
  "quantity": 2
}
```

**Request 예시 - Wine 제거:**
```json
{
  "action": "REMOVE",
  "dishId": 2,
  "quantity": 0
}
```

**Request 예시 - Champagne 수량 변경:**
```json
{
  "action": "REPLACE",
  "dishId": 8,
  "quantity": 3
}
```

**Response (200 OK):**

전체 CartResponse 반환 (customizations 포함)

**비즈니스 규칙:**
- ADD: 같은 요리에 대한 커스터마이징이 이미 존재하면 업데이트, 없으면 새로 추가
- REMOVE: 해당 요리의 커스터마이징을 완전히 제거
- REPLACE: 기존 커스터마이징의 수량만 변경

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 액션 또는 입력값
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: 다른 사용자의 장바구니 아이템
- `404 Not Found`: 아이템 또는 요리가 존재하지 않음

---

## 4. 주문 API

### 4.1 주문하기 (체크아웃)

**엔드포인트:** `POST /api/orders/checkout`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 장바구니의 아이템들로 주문을 생성합니다.

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| deliveryAddress | String | ✅ | 배송 주소 | 1자 이상 |
| paymentMethod | String | ✅ | 결제 수단 | 유효한 결제 수단 |

**Request 예시:**
```json
{
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "paymentMethod": "CREDIT_CARD"
}
```

**Response (200 OK):**

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 (true) |
| message | String | 성공 메시지 |
| data | Long | 생성된 주문 ID |

**Response 예시:**
```json
{
  "success": true,
  "message": "Order placed successfully",
  "data": 123
}
```

**비즈니스 규칙:**
- 장바구니가 비어있으면 주문 불가
- 주문 생성 시 장바구니의 모든 아이템이 주문 아이템으로 변환됨
- 주문 생성 후 장바구니는 자동으로 비워짐

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 입력값 또는 빈 장바구니
- `401 Unauthorized`: 인증되지 않은 요청

---

### 4.2 주문 내역 조회

**엔드포인트:** `GET /api/orders/history`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 현재 로그인한 사용자의 모든 주문 내역을 조회합니다.

**Request Parameters:** 없음

**Response (200 OK):**

Array of OrderHistoryResponse

**OrderHistoryResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | 주문 ID |
| orderDate | LocalDateTime | 주문 일시 |
| status | String | 주문 상태 |
| totalPrice | Integer | 총 주문 금액 (원) |
| itemCount | Integer | 주문 아이템 개수 |

**주문 상태 (OrderStatus):**
- `PENDING`: 대기 중
- `PREPARING`: 준비 중
- `DELIVERING`: 배달 중
- `COMPLETED`: 완료
- `CANCELLED`: 취소됨

**Response 예시:**
```json
[
  {
    "orderId": 1,
    "orderDate": "2025-11-18T10:30:00",
    "status": "COMPLETED",
    "totalPrice": 150000,
    "itemCount": 2
  },
  {
    "orderId": 2,
    "orderDate": "2025-11-17T15:20:00",
    "status": "DELIVERING",
    "totalPrice": 90000,
    "itemCount": 1
  }
]
```

**에러 응답:**
- `401 Unauthorized`: 인증되지 않은 요청

---

### 4.3 주문 상세 조회

**엔드포인트:** `GET /api/orders/{orderId}`

**인증 필요:** ✅ (Authorization: Bearer {accessToken})

**설명:** 특정 주문의 상세 정보를 조회합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| orderId | Long | ✅ | 주문 ID |

**Response (200 OK):**

**OrderDetailResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | 주문 ID |
| orderDate | LocalDateTime | 주문 일시 |
| status | String | 주문 상태 |
| deliveryAddress | String | 배송 주소 |
| paymentMethod | String | 결제 수단 |
| totalPrice | Integer | 총 주문 금액 (원) |
| items | Array | 주문 아이템 목록 |

**OrderItemResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| itemId | Long | 주문 아이템 ID |
| dinnerName | String | 디너 이름 |
| servingStyleName | String | 서빙 스타일 이름 |
| quantity | Integer | 수량 |
| price | Integer | 아이템 가격 (원) |
| customizations | Array | 커스터마이징 목록 |

**OrderCustomizationResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| action | String | 액션 타입 |
| dishName | String | 요리 이름 |
| quantity | Integer | 수량 |

**Response 예시:**
```json
{
  "orderId": 1,
  "orderDate": "2025-11-18T10:30:00",
  "status": "COMPLETED",
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "paymentMethod": "CREDIT_CARD",
  "totalPrice": 150000,
  "items": [
    {
      "itemId": 1,
      "dinnerName": "Valentine Dinner",
      "servingStyleName": "Deluxe",
      "quantity": 2,
      "price": 150000,
      "customizations": [
        {
          "action": "ADD",
          "dishName": "Champagne",
          "quantity": 1
        },
        {
          "action": "REMOVE",
          "dishName": "Wine",
          "quantity": 0
        }
      ]
    }
  ]
}
```

**에러 응답:**
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: 다른 사용자의 주문
- `404 Not Found`: 주문이 존재하지 않음

---

## 5. 스태프 API

### 5.1 활성 주문 목록 조회

**엔드포인트:** `GET /api/staff/orders/active`

**인증 필요:** ✅ STAFF 권한 필요 (Authorization: Bearer {accessToken})

**설명:** 진행 중인 모든 주문 목록을 조회합니다 (PENDING, PREPARING, DELIVERING 상태).

**Request Parameters:** 없음

**Response (200 OK):**

Array of ActiveOrdersResponse

**ActiveOrdersResponse:**

| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | 주문 ID |
| customerName | String | 고객 이름 |
| customerPhone | String | 고객 전화번호 |
| deliveryAddress | String | 배송 주소 |
| orderDate | LocalDateTime | 주문 일시 |
| status | String | 주문 상태 |
| totalPrice | Integer | 총 주문 금액 (원) |
| itemCount | Integer | 주문 아이템 개수 |

**Response 예시:**
```json
[
  {
    "orderId": 5,
    "customerName": "홍길동",
    "customerPhone": "010-1234-5678",
    "deliveryAddress": "서울시 강남구 테헤란로 123",
    "orderDate": "2025-11-18T14:30:00",
    "status": "PREPARING",
    "totalPrice": 90000,
    "itemCount": 1
  },
  {
    "orderId": 6,
    "customerName": "김철수",
    "customerPhone": "010-9876-5432",
    "deliveryAddress": "서울시 서초구 반포대로 456",
    "orderDate": "2025-11-18T15:00:00",
    "status": "PENDING",
    "totalPrice": 120000,
    "itemCount": 2
  }
]
```

**에러 응답:**
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: STAFF 권한이 없음

---

### 5.2 주문 상태 변경

**엔드포인트:** `PUT /api/staff/orders/{orderId}/status`

**인증 필요:** ✅ STAFF 권한 필요 (Authorization: Bearer {accessToken})

**설명:** 주문의 상태를 변경합니다.

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| orderId | Long | ✅ | 주문 ID |

**Request Body:**

| 필드 | 타입 | 필수 | 설명 | 제약사항 |
|------|------|------|------|----------|
| status | String | ✅ | 새로운 주문 상태 | PENDING, PREPARING, DELIVERING, COMPLETED, CANCELLED 중 하나 |

**Request 예시:**
```json
{
  "status": "PREPARING"
}
```

**Response (200 OK):**

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 (true) |
| message | String | 성공 메시지 |
| data | null | 데이터 없음 |

**Response 예시:**
```json
{
  "success": true,
  "message": "Order status updated",
  "data": null
}
```

**주문 상태 변경 흐름:**
1. `PENDING` (주문 접수) → `PREPARING` (준비 중)
2. `PREPARING` → `DELIVERING` (배달 중)
3. `DELIVERING` → `COMPLETED` (완료)
4. 모든 상태 → `CANCELLED` (취소)

**에러 응답:**
- `400 Bad Request`: 유효하지 않은 상태값
- `401 Unauthorized`: 인증되지 않은 요청
- `403 Forbidden`: STAFF 권한이 없음
- `404 Not Found`: 주문이 존재하지 않음

---

## 6. 공통 응답 형식

### 성공 응답 (ApiResponse)

대부분의 API는 다음과 같은 통일된 응답 형식을 사용합니다:

```json
{
  "success": true,
  "message": "Success message",
  "data": { ... }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 성공 여부 (true/false) |
| message | String | 응답 메시지 |
| data | Object/Array/null | 응답 데이터 |

**예외:**
- 장바구니 API: CartResponse를 직접 반환
- 주문 내역 조회: Array를 직접 반환
- 주문 상세 조회: OrderDetailResponse를 직접 반환
- 메뉴 API: MenuListResponse 배열 또는 MenuDetailResponse를 직접 반환
- 스태프 활성 주문 조회: ActiveOrdersResponse 배열을 직접 반환

---

## 7. 에러 코드

### HTTP 상태 코드

| 코드 | 설명 | 발생 상황 |
|------|------|----------|
| 200 | OK | 요청 성공 |
| 400 | Bad Request | 유효하지 않은 입력값 |
| 401 | Unauthorized | 인증 실패 또는 토큰 만료 |
| 403 | Forbidden | 권한 부족 |
| 404 | Not Found | 리소스를 찾을 수 없음 |
| 409 | Conflict | 리소스 충돌 (중복 이메일 등) |
| 500 | Internal Server Error | 서버 내부 오류 |

### 에러 응답 형식

```json
{
  "success": false,
  "message": "Error message describing what went wrong",
  "data": null
}
```

**예시:**
```json
{
  "success": false,
  "message": "Customer not found",
  "data": null
}
```

---

## 부록

### 테스트 계정

**일반 사용자 (USER):**

| Email | Password | 이름 | 주소 | 전화번호 |
|-------|----------|------|------|----------|
| user@test.com | test1234 | 테스트 사용자 | 서울시 강남구 테헤란로 123 | 010-1234-5678 |
| john@test.com | test1234 | John Smith | 서울시 서초구 강남대로 456 | 010-2222-3333 |
| emily@test.com | test1234 | Emily Johnson | 서울시 송파구 올림픽로 789 | 010-3333-4444 |
| mike@test.com | test1234 | Mike Brown | 서울시 강동구 천호대로 321 | 010-4444-5555 |

**스태프 (STAFF):**

| Email | Password | 이름 | 주소 | 전화번호 |
|-------|----------|------|------|----------|
| staff@test.com | staff1234 | 직원 계정 | 서울시 강남구 테헤란로 456 | 010-9876-5432 |

**주의:** 모든 비밀번호는 BCrypt로 암호화되어 저장됩니다.

### 인증 헤더 형식

모든 인증이 필요한 API는 다음과 같은 형식의 헤더를 포함해야 합니다:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 가격 정책

- 모든 가격은 **Integer 타입**으로 원 단위입니다
- 소수점은 사용하지 않습니다
- 아이템 총 가격 = (디너 기본 가격 + 서빙 스타일 추가 가격) × 수량

**예시:**
- Valentine Dinner: 45,000원
- Deluxe 서빙: +30,000원
- 수량: 2개
- 총 가격: (45,000 + 30,000) × 2 = 150,000원

### 중요 비즈니스 규칙

1. **Champagne Feast Dinner 제약**
   - Champagne Feast Dinner (ID: 4)는 Simple 스타일 선택 불가
   - Grand (ID: 2) 또는 Deluxe (ID: 3)만 선택 가능

2. **장바구니 정책**
   - 사용자당 하나의 장바구니만 존재
   - 주문 완료 시 장바구니는 자동으로 비워짐

3. **커스터마이징 정책**
   - ADD: 같은 요리가 이미 있으면 업데이트, 없으면 추가
   - REMOVE: 해당 요리의 커스터마이징 완전 제거
   - REPLACE: 수량만 변경

4. **주문 상태 흐름**
   - PENDING → PREPARING → DELIVERING → COMPLETED
   - 모든 상태에서 CANCELLED로 변경 가능

### 데이터 ID 참조

#### Serving Styles (서빙 스타일)

| ID | 이름 | 추가 가격 | 설명 |
|----|------|----------|------|
| 1 | Simple | 0원 | 플라스틱 접시와 플라스틱 컵, 종이 냅킨이 플라스틱 쟁반에 제공 |
| 2 | Grand | 15,000원 | 도자기 접시와 도자기 컵, 흰색 면 냅킨이 나무 쟁반에 제공 |
| 3 | Deluxe | 30,000원 | 꽃병, 도자기 접시와 도자기 컵, 린넨 냅킨이 나무 쟁반에 제공 |

#### Dinners (디너 메뉴)

| ID | 이름 | 기본 가격 | 설명 | 선택 가능 스타일 |
|----|------|----------|------|-----------------|
| 1 | Valentine Dinner | 45,000원 | 작은 하트 모양과 큐피드가 장식된 접시에 냅킨과 함께 와인과 스테이크가 제공 | Simple(1), Grand(2), Deluxe(3) |
| 2 | French Dinner | 48,000원 | 커피 한잔, 와인 한잔, 샐러드, 스테이크 제공 | Simple(1), Grand(2), Deluxe(3) |
| 3 | English Dinner | 42,000원 | 에그 스크램블, 베이컨, 빵, 스테이크가 제공 | Simple(1), Grand(2), Deluxe(3) |
| 4 | Champagne Feast Dinner | 120,000원 | 항상 2인 식사이고, 샴페인 1병, 4개의 바게트빵, 커피 포트, 와인, 스테이크 제공 | **Grand(2), Deluxe(3)만 가능** |

#### Dishes (요리 품목)

| ID | 이름 | 설명 | 단가 | 기본 수량 |
|----|------|------|------|----------|
| 1 | Steak | 프리미엄 스테이크 | 25,000원 | 1 |
| 2 | Wine | 레드 와인 | 8,000원 | 1 |
| 3 | Coffee | 아메리카노 | 3,000원 | 1 |
| 4 | Salad | 신선한 샐러드 | 5,000원 | 1 |
| 5 | Scrambled Eggs | 에그 스크램블 | 4,000원 | 1 |
| 6 | Bacon | 베이컨 | 5,000원 | 1 |
| 7 | Bread | 식빵 | 2,000원 | 1 |
| 8 | Champagne | 샴페인 (병) | 50,000원 | 1 |
| 9 | Baguette | 바게트빵 | 3,000원 | 1 |
| 10 | Coffee Pot | 커피 포트 (6잔) | 10,000원 | 1 |
| 11 | Heart Decoration | 하트 장식 | 2,000원 | 1 |
| 12 | Cupid Decoration | 큐피드 장식 | 3,000원 | 1 |
| 13 | Napkin | 냅킨 | 500원 | 1 |

#### Dinner 구성 품목

**Valentine Dinner (ID: 1)**
- Steak (ID: 1)
- Wine (ID: 2)
- Heart Decoration (ID: 11)
- Cupid Decoration (ID: 12)
- Napkin (ID: 13)

**French Dinner (ID: 2)**
- Coffee (ID: 3)
- Wine (ID: 2)
- Salad (ID: 4)
- Steak (ID: 1)

**English Dinner (ID: 3)**
- Scrambled Eggs (ID: 5)
- Bacon (ID: 6)
- Bread (ID: 7)
- Steak (ID: 1)

**Champagne Feast Dinner (ID: 4)**
- Champagne (ID: 8) - 1병
- Baguette (ID: 9) - 4개
- Coffee Pot (ID: 10) - 6잔
- Wine (ID: 2)
- Steak (ID: 1) - 2인분

---

**문서 버전:** 1.1.0
**최종 업데이트:** 2025-11-18
