# 🍽️ Ddoganzip Backend API 명세서

> **프론트엔드 개발팀을 위한 완전한 API 통합 가이드**
>
> 이 문서는 현재 백엔드 구현을 기반으로 작성되었습니다. 모든 엔드포인트와 데이터 구조는 실제 코드와 일치합니다.

---

## 📋 목차

1. [서버 정보](#서버-정보)
2. [인증 시스템](#인증-시스템)
3. [API 엔드포인트](#api-엔드포인트)
   - [인증 API](#1-인증-api-apiauth)
   - [메뉴 API](#2-메뉴-api-apimenu)
   - [장바구니 API](#3-장바구니-api-apicart)
   - [주문 API](#4-주문-api-apiorders)
   - [스태프 API](#5-스태프-api-apistaff)
4. [데이터 모델](#데이터-모델)
5. [에러 처리](#에러-처리)
6. [테스트 계정](#테스트-계정)

---

## 서버 정보

### 기본 설정
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **인증 방식**: JWT Bearer Token
- **CORS 허용 포트**: `5176`, `3000`, `5000`

### 응답 형식
모든 API는 다음 형식 중 하나로 응답합니다:

```typescript
// 성공 응답 (데이터 포함)
{
  success: true,
  message: string,
  data: T
}

// 성공 응답 (메시지만)
{
  success: true,
  message: string
}

// 에러 응답
{
  error: {
    code: string,
    message: string,
    details?: any
  }
}
```

---

## 인증 시스템

### JWT 토큰 구조
- **Access Token**: 유효기간 1시간
- **Refresh Token**: 유효기간 7일

### 헤더 설정
인증이 필요한 API는 다음 헤더를 포함해야 합니다:

```http
Authorization: Bearer {accessToken}
```

### 역할(Role) 시스템
- **USER**: 일반 고객 (주문, 장바구니 접근)
- **STAFF**: 직원 (주문 관리, 상태 업데이트)

---

## API 엔드포인트

## 1. 인증 API (`/api/auth`)

### 1.1 회원가입
```http
POST /api/auth/register
```

**요청 Body:**
```typescript
{
  email: string;        // 이메일 형식 필수
  password: string;     // 비밀번호
  name: string;         // 이름
  address?: string;     // 주소 (선택)
  phone?: string;       // 전화번호 (선택)
}
```

**응답 (200):**
```typescript
{
  success: true,
  message: "Registration successful"
}
```

---

### 1.2 로그인
```http
POST /api/auth/login
```

**요청 Body:**
```typescript
{
  email: string;
  password: string;
}
```

**응답 (200):**
```typescript
{
  accessToken: string;   // 1시간 유효
  refreshToken: string;  // 7일 유효
  tokenType: "Bearer";
  expiresIn: 3600000;   // 밀리초 (1시간)
}
```

---

### 1.3 토큰 갱신
```http
POST /api/auth/refresh
```

**요청 Body:**
```typescript
{
  refreshToken: string;
}
```

**응답 (200):**
```typescript
{
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: 3600000;
}
```

---

### 1.4 로그아웃
```http
POST /api/auth/logout
```

**헤더:** `Authorization: Bearer {accessToken}`

**응답 (200):**
```typescript
{
  success: true,
  message: "Logout successful"
}
```

---

## 2. 메뉴 API (`/api/menu`)

### 2.1 메뉴 목록 조회
```http
GET /api/menu/list
```

**응답 (200):**
```typescript
[
  {
    dinnerId: number;
    name: string;
    description: string;
    basePrice: number;        // Integer (원 단위)
    imageUrl: string;
  }
]
```

**예시 응답:**
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
    "dinnerId": 4,
    "name": "Champagne Feast Dinner",
    "description": "항상 2인 식사이고, 샴페인 1병, 4개의 바게트빵, 커피 포트, 와인, 스테이크 제공",
    "basePrice": 120000,
    "imageUrl": "https://example.com/champagne.jpg"
  }
]
```

---

### 2.2 메뉴 상세 조회
```http
GET /api/menu/details/{dinnerId}
```

**응답 (200):**
```typescript
{
  dinnerId: number;
  name: string;
  description: string;
  basePrice: number;
  imageUrl: string;
  dishes: Array<{
    dishId: number;
    name: string;
    description: string;
    basePrice: number;
    defaultQuantity: number;
  }>;
  availableStyles: Array<{
    styleId: number;
    name: string;              // "Simple", "Grand", "Deluxe"
    additionalPrice: number;   // 추가 비용
    description: string;
  }>;
}
```

**예시 응답:**
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
      "description": "프리미엄 스테이크",
      "basePrice": 25000,
      "defaultQuantity": 1
    },
    {
      "dishId": 2,
      "name": "Wine",
      "description": "레드 와인",
      "basePrice": 8000,
      "defaultQuantity": 1
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

**⚠️ 중요:** Champagne Feast Dinner (dinnerId: 4)는 Simple 스타일을 선택할 수 없으며, Grand 또는 Deluxe만 가능합니다!

---

## 3. 장바구니 API (`/api/cart`)

**🔑 모든 장바구니 API는 인증 필요**

### 3.1 장바구니 조회
```http
GET /api/cart
```

**헤더:** `Authorization: Bearer {accessToken}`

**응답 (200):**
```typescript
{
  cartId: number;
  items: Array<{
    cartItemId: number;
    dinnerId: number;
    dinnerName: string;
    servingStyleId: number;
    servingStyleName: string;
    quantity: number;
    dinnerBasePrice: number;      // 디너 기본 가격
    servingStylePrice: number;     // 서빙 스타일 추가 가격
    itemTotalPrice: number;        // (디너 기본 + 서빙 추가) × 수량
  }>;
  totalPrice: number;              // 전체 합계
}
```

**예시 응답:**
```json
{
  "cartId": 1,
  "items": [
    {
      "cartItemId": 1,
      "dinnerId": 1,
      "dinnerName": "Valentine Dinner",
      "servingStyleId": 3,
      "servingStyleName": "Deluxe",
      "quantity": 1,
      "dinnerBasePrice": 45000,
      "servingStylePrice": 30000,
      "itemTotalPrice": 75000
    }
  ],
  "totalPrice": 75000
}
```

---

### 3.2 장바구니에 상품 추가
```http
POST /api/cart/items
```

**헤더:** `Authorization: Bearer {accessToken}`

**요청 Body:**
```typescript
{
  dinnerId: number;
  servingStyleId: number;
  quantity: number;        // 1 이상
}
```

**응답 (200):** 전체 CartResponse (3.1과 동일)

**✅ 중요:** 이 API는 성공 메시지가 아닌 **CartResponse를 직접 반환**합니다!

---

### 3.3 장바구니 상품 수량 변경
```http
PUT /api/cart/items/{itemId}/quantity
```

**헤더:** `Authorization: Bearer {accessToken}`

**요청 Body:**
```typescript
{
  quantity: number;  // 1 이상
}
```

**응답 (200):** 전체 CartResponse

---

### 3.4 장바구니 상품 옵션 변경
```http
PUT /api/cart/items/{itemId}/options
```

**헤더:** `Authorization: Bearer {accessToken}`

**요청 Body:**
```typescript
{
  servingStyleId: number;
}
```

**응답 (200):** 전체 CartResponse

---

### 3.5 장바구니 상품 삭제
```http
DELETE /api/cart/items/{itemId}
```

**헤더:** `Authorization: Bearer {accessToken}`

**응답 (200):** 전체 CartResponse

---

## 4. 주문 API (`/api/orders`)

**🔑 모든 주문 API는 인증 필요**

### 4.1 주문하기 (체크아웃)
```http
POST /api/orders/checkout
```

**헤더:** `Authorization: Bearer {accessToken}`

**요청 Body:**
```typescript
{
  deliveryAddress: string;
  deliveryDate: string;  // ISO 8601 형식: "2025-11-19T12:00:00"
}
```

**응답 (200):**
```typescript
{
  success: true,
  message: "Order placed successfully",
  data: number  // orderId
}
```

**예시:**
```json
{
  "success": true,
  "message": "Order placed successfully",
  "data": 5
}
```

---

### 4.2 주문 내역 조회
```http
GET /api/orders/history
```

**헤더:** `Authorization: Bearer {accessToken}`

**응답 (200):**
```typescript
[
  {
    orderId: number;
    orderDate: string;        // ISO 8601
    deliveryDate: string;
    deliveryAddress: string;
    status: OrderStatus;
    totalPrice: number;
  }
]
```

**OrderStatus 종류:**
- `CHECKING_STOCK`: 재고 확인 중
- `RECEIVED`: 주문 접수됨
- `IN_KITCHEN`: 조리 중
- `DELIVERING`: 배달 중
- `DELIVERED`: 배달 완료

**예시 응답:**
```json
[
  {
    "orderId": 5,
    "orderDate": "2025-11-18T17:30:00",
    "deliveryDate": "2025-11-19T12:00:00",
    "deliveryAddress": "서울시 서초구 강남대로 456",
    "status": "CHECKING_STOCK",
    "totalPrice": 150000
  },
  {
    "orderId": 4,
    "orderDate": "2025-11-18T17:00:00",
    "deliveryDate": "2025-11-18T19:30:00",
    "deliveryAddress": "서울시 강남구 테헤란로 123",
    "status": "RECEIVED",
    "totalPrice": 84000
  }
]
```

---

### 4.3 주문 상세 조회
```http
GET /api/orders/{orderId}
```

**헤더:** `Authorization: Bearer {accessToken}`

**응답 (200):**
```typescript
{
  orderId: number;
  orderDate: string;
  deliveryDate: string;
  deliveryAddress: string;
  status: OrderStatus;
  totalPrice: number;
  items: Array<{
    orderItemId: number;
    dinnerName: string;
    servingStyleName: string;
    quantity: number;
    price: number;         // 아이템 총 가격
  }>;
}
```

**예시 응답:**
```json
{
  "orderId": 5,
  "orderDate": "2025-11-18T17:30:00",
  "deliveryDate": "2025-11-19T12:00:00",
  "deliveryAddress": "서울시 서초구 강남대로 456",
  "status": "CHECKING_STOCK",
  "totalPrice": 150000,
  "items": [
    {
      "orderItemId": 10,
      "dinnerName": "Champagne Feast Dinner",
      "servingStyleName": "Deluxe",
      "quantity": 1,
      "price": 150000
    }
  ]
}
```

---

## 5. 스태프 API (`/api/staff`)

**🔑 모든 스태프 API는 STAFF 역할 필요**

### 5.1 진행 중인 주문 조회
```http
GET /api/staff/orders/active
```

**헤더:** `Authorization: Bearer {accessToken}` (STAFF 권한)

**응답 (200):**
```typescript
[
  {
    orderId: number;
    customerName: string;
    deliveryAddress: string;
    deliveryDate: string;
    status: OrderStatus;
    totalPrice: number;
    orderDate: string;
  }
]
```

**예시 응답:**
```json
[
  {
    "orderId": 5,
    "customerName": "John Smith",
    "deliveryAddress": "서울시 서초구 강남대로 456",
    "deliveryDate": "2025-11-19T12:00:00",
    "status": "CHECKING_STOCK",
    "totalPrice": 150000,
    "orderDate": "2025-11-18T17:30:00"
  },
  {
    "orderId": 3,
    "customerName": "Emily Johnson",
    "deliveryAddress": "서울시 송파구 올림픽로 789",
    "deliveryDate": "2025-11-18T20:00:00",
    "status": "IN_KITCHEN",
    "totalPrice": 135000,
    "orderDate": "2025-11-18T16:00:00"
  }
]
```

---

### 5.2 주문 상태 업데이트
```http
PUT /api/staff/orders/{orderId}/status
```

**헤더:** `Authorization: Bearer {accessToken}` (STAFF 권한)

**요청 Body:**
```typescript
{
  status: OrderStatus;  // 다음 단계 상태
}
```

**상태 진행 순서:**
```
CHECKING_STOCK → RECEIVED → IN_KITCHEN → DELIVERING → DELIVERED
```

**응답 (200):**
```typescript
{
  success: true,
  message: "Order status updated"
}
```

---

## 데이터 모델

### Dinner (디너)
현재 시스템에 4가지 디너가 있습니다:

| ID | 이름 | 설명 | 기본 가격 | 특이사항 |
|----|------|------|-----------|----------|
| 1 | Valentine Dinner | 하트 장식, 와인, 스테이크 | 45,000원 | 모든 스타일 가능 |
| 2 | French Dinner | 커피, 와인, 샐러드, 스테이크 | 48,000원 | 모든 스타일 가능 |
| 3 | English Dinner | 에그 스크램블, 베이컨, 빵, 스테이크 | 42,000원 | 모든 스타일 가능 |
| 4 | Champagne Feast Dinner | 2인 식사, 샴페인, 바게트빵 4개 | 120,000원 | **Grand/Deluxe만 가능** |

### Serving Style (서빙 스타일)

| ID | 이름 | 추가 비용 | 설명 |
|----|------|-----------|------|
| 1 | Simple | 0원 | 플라스틱 접시, 플라스틱 컵, 종이 냅킨 |
| 2 | Grand | 15,000원 | 도자기 접시, 도자기 컵, 면 냅킨, 나무 쟁반 |
| 3 | Deluxe | 30,000원 | 꽃병, 도자기 접시, 린넨 냅킨, 유리 와인잔 |

### 가격 계산
```
최종 가격 = (디너 기본 가격 + 서빙 스타일 추가 비용) × 수량
```

**예시:**
- Valentine Dinner (45,000원) + Deluxe (30,000원) × 1개 = **75,000원**
- Champagne Feast (120,000원) + Grand (15,000원) × 1개 = **135,000원**

---

## 에러 처리

### 에러 응답 형식
```typescript
{
  error: {
    code: string;
    message: string;
    details?: any;
  }
}
```

### 일반적인 HTTP 상태 코드
- `200`: 성공
- `400`: 잘못된 요청 (유효성 검증 실패)
- `401`: 인증 실패 (토큰 없음 또는 만료)
- `403`: 권한 없음 (STAFF 권한 필요)
- `404`: 리소스를 찾을 수 없음
- `500`: 서버 내부 오류

### 예시 에러 응답
```json
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid or expired token"
  }
}
```

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": {
      "email": "Invalid email format"
    }
  }
}
```

---

## 테스트 계정

### 고객 계정 (USER)
```
이메일: user@test.com
비밀번호: test1234
```

```
이메일: john@test.com
비밀번호: test1234
```

### 직원 계정 (STAFF)
```
이메일: staff@test.com
비밀번호: staff1234
```

---

## 개발 팁

### 1. 인증 플로우
```typescript
// 1. 로그인
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
const { accessToken, refreshToken } = await loginResponse.json();

// 2. 로컬 스토리지에 저장
localStorage.setItem('accessToken', accessToken);
localStorage.setItem('refreshToken', refreshToken);

// 3. 인증 API 호출
const response = await fetch('/api/cart', {
  headers: {
    'Authorization': `Bearer ${accessToken}`,
    'Content-Type': 'application/json'
  }
});
```

### 2. 토큰 갱신
```typescript
// Access Token 만료 시 (401 응답)
if (response.status === 401) {
  const refreshResponse = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      refreshToken: localStorage.getItem('refreshToken')
    })
  });

  const { accessToken, refreshToken } = await refreshResponse.json();
  localStorage.setItem('accessToken', accessToken);
  localStorage.setItem('refreshToken', refreshToken);

  // 원래 요청 재시도
  return fetch(originalUrl, originalOptions);
}
```

### 3. 장바구니 상품 추가
```typescript
const addToCart = async (dinnerId: number, servingStyleId: number, quantity: number) => {
  const response = await fetch('/api/cart/items', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ dinnerId, servingStyleId, quantity })
  });

  // 응답은 전체 CartResponse
  const cart = await response.json();
  console.log('Updated cart:', cart);
};
```

### 4. 주문하기
```typescript
const checkout = async (deliveryAddress: string, deliveryDate: string) => {
  const response = await fetch('/api/orders/checkout', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ deliveryAddress, deliveryDate })
  });

  const result = await response.json();
  const orderId = result.data;
  console.log('Order created:', orderId);
};
```

---

## 주의사항

### ⚠️ 중요 제약사항
1. **Champagne Feast Dinner는 Simple 스타일 선택 불가** - Grand 또는 Deluxe만 선택 가능
2. 모든 가격은 **Integer 타입** (원 단위, 소수점 없음)
3. 장바구니 API는 **항상 전체 CartResponse 반환** (성공 메시지 아님)
4. 날짜는 **ISO 8601 형식** 사용: `2025-11-19T12:00:00`
5. STAFF API는 **STAFF 역할의 토큰 필요**

### 💡 개발 가이드
- 모든 API 요청 시 `Content-Type: application/json` 헤더 필수
- 인증 토큰은 `Bearer {token}` 형식으로 전송
- 에러 응답은 항상 `error` 객체 포함
- 페이지네이션은 현재 미지원 (향후 추가 예정)

---

## 연락처

백엔드 관련 문의사항이 있으시면 팀 채널로 연락주세요!

**마지막 업데이트:** 2025-11-18
**API 버전:** 1.0
**백엔드 프레임워크:** Spring Boot 3.5.6
