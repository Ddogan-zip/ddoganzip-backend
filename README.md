# Ddoganzip - 미스터 대박 디너 서비스 백엔드

훌륭한 디너 만찬을 고객의 집으로 배달하는 서비스의 백엔드 API 서버입니다.

## API 문서

Swagger UI를 통한 상세 API 명세: https://ddogan-zip.github.io/ddoganzip-backend/

## 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: PostgreSQL / H2 (개발용)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Documentation**: OpenAPI 3.0 (Swagger UI)

## 실행 방법

### 1. 데이터베이스 설정

```bash
# PostgreSQL에서 데이터베이스 생성
createdb ddoganzip
```

### 2. 설정 파일 수정

`src/main/resources/application.yml`에서 데이터베이스 연결 정보 수정:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ddoganzip
    username: your_username
    password: your_password
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 H2 인메모리 DB로 실행 (DB 설치 불필요):

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

### 4. 접속

- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## API 엔드포인트

### 인증 (Auth)

JWT 토큰 기반 인증을 사용합니다.

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| POST | `/api/auth/register` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 (토큰 발급) | X |
| POST | `/api/auth/refresh` | 토큰 갱신 | X |
| POST | `/api/auth/logout` | 로그아웃 | X |

#### 회원가입
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동",
  "address": "서울시 강남구",
  "phone": "010-1234-5678"
}
```

#### 로그인
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

응답:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### 토큰 갱신
```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### 메뉴 (Menu)

디너 메뉴를 조회합니다. 인증 없이 접근 가능합니다.

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/menu/list` | 전체 메뉴 목록 조회 | X |
| GET | `/api/menu/details/{dinnerId}` | 특정 메뉴 상세 조회 | X |

#### 메뉴 목록 조회
```
GET /api/menu/list
```

응답:
```json
[
  {
    "id": 1,
    "name": "Valentine Dinner",
    "description": "하트 모양과 큐피드 장식이 포함된 로맨틱한 저녁 식사",
    "basePrice": 35000
  },
  {
    "id": 2,
    "name": "French Dinner",
    "description": "커피, 와인, 샐러드, 스테이크로 구성된 프랑스식 정찬",
    "basePrice": 30000
  }
]
```

#### 메뉴 상세 조회
```
GET /api/menu/details/1
```

응답:
```json
{
  "id": 1,
  "name": "Valentine Dinner",
  "description": "하트 모양과 큐피드 장식이 포함된 로맨틱한 저녁 식사",
  "basePrice": 35000,
  "dishes": [
    { "id": 1, "name": "Steak", "defaultQuantity": 1 },
    { "id": 2, "name": "Wine", "defaultQuantity": 1 },
    { "id": 3, "name": "Coffee", "defaultQuantity": 1 },
    { "id": 4, "name": "Salad", "defaultQuantity": 1 }
  ],
  "availableStyles": [
    { "id": 1, "name": "Simple", "additionalPrice": 0, "description": "플라스틱 접시와 컵, 종이 냅킨" },
    { "id": 2, "name": "Grand", "additionalPrice": 5000, "description": "도자기 접시와 컵, 흰색 면 냅킨" },
    { "id": 3, "name": "Deluxe", "additionalPrice": 10000, "description": "꽃병, 도자기 접시와 컵, 린넨 냅킨" }
  ]
}
```

---

### 장바구니 (Cart)

고객의 장바구니를 관리합니다. 인증이 필요합니다.

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/cart` | 장바구니 조회 | O |
| POST | `/api/cart/items` | 상품 추가 | O |
| PUT | `/api/cart/items/{itemId}/quantity` | 수량 변경 | O |
| PUT | `/api/cart/items/{itemId}/options` | 옵션 변경 | O |
| DELETE | `/api/cart/items/{itemId}` | 상품 삭제 | O |

#### 장바구니 조회
```
GET /api/cart
Authorization: Bearer {accessToken}
```

응답:
```json
{
  "cartId": 1,
  "items": [
    {
      "itemId": 1,
      "dinnerId": 1,
      "dinnerName": "Valentine Dinner",
      "dinnerBasePrice": 35000,
      "servingStyleId": 2,
      "servingStyleName": "Grand",
      "servingStylePrice": 5000,
      "quantity": 2,
      "itemTotalPrice": 80000,
      "customizations": [
        {
          "action": "ADD",
          "dishId": 8,
          "dishName": "Champagne",
          "quantity": 1
        }
      ]
    }
  ],
  "totalPrice": 80000
}
```

#### 상품 추가
```
POST /api/cart/items
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "dinnerId": 1,
  "servingStyleId": 2,
  "quantity": 1,
  "customizations": [
    {
      "action": "ADD",
      "dishId": 8,
      "quantity": 1
    }
  ]
}
```

#### 수량 변경
```
PUT /api/cart/items/1/quantity
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "quantity": 3
}
```

#### 옵션 변경
```
PUT /api/cart/items/1/options
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "servingStyleId": 3,
  "customizations": [
    {
      "action": "REMOVE",
      "dishId": 2,
      "quantity": 1
    }
  ]
}
```

#### 상품 삭제
```
DELETE /api/cart/items/1
Authorization: Bearer {accessToken}
```

---

### 주문 (Orders)

주문을 생성하고 조회합니다. 인증이 필요합니다.

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| POST | `/api/orders/checkout` | 주문하기 | O |
| GET | `/api/orders/history` | 주문 내역 조회 | O |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 | O |

#### 주문하기
```
POST /api/orders/checkout
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "deliveryDate": "2024-12-25T19:00:00"
}
```

응답:
```json
{
  "success": true,
  "message": "Order placed successfully",
  "data": 1
}
```

#### 주문 내역 조회
```
GET /api/orders/history
Authorization: Bearer {accessToken}
```

응답:
```json
[
  {
    "orderId": 1,
    "orderDate": "2024-12-20T15:30:00",
    "deliveryDate": "2024-12-25T19:00:00",
    "deliveryAddress": "서울시 강남구 테헤란로 123",
    "status": "IN_KITCHEN",
    "totalPrice": 80000,
    "itemCount": 2
  }
]
```

#### 주문 상세 조회
```
GET /api/orders/1
Authorization: Bearer {accessToken}
```

응답:
```json
{
  "orderId": 1,
  "orderDate": "2024-12-20T15:30:00",
  "deliveryDate": "2024-12-25T19:00:00",
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "status": "IN_KITCHEN",
  "totalPrice": 80000,
  "items": [
    {
      "itemId": 1,
      "dinnerName": "Valentine Dinner",
      "servingStyleName": "Grand",
      "quantity": 2,
      "price": 80000,
      "customizations": [
        {
          "action": "ADD",
          "dishName": "Champagne",
          "quantity": 1
        }
      ]
    }
  ]
}
```

---

### 직원용 (Staff)

주문 상태를 관리합니다. **STAFF 권한이 필요합니다.**

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/staff/orders/active` | 활성 주문 조회 | O (STAFF) |
| PUT | `/api/staff/orders/{orderId}/status` | 주문 상태 변경 | O (STAFF) |

#### 활성 주문 조회
```
GET /api/staff/orders/active
Authorization: Bearer {accessToken}
```

응답:
```json
[
  {
    "orderId": 1,
    "customerName": "홍길동",
    "customerEmail": "user@example.com",
    "orderDate": "2024-12-20T15:30:00",
    "deliveryDate": "2024-12-25T19:00:00",
    "deliveryAddress": "서울시 강남구 테헤란로 123",
    "status": "IN_KITCHEN",
    "totalPrice": 80000,
    "itemCount": 2
  }
]
```

#### 주문 상태 변경
```
PUT /api/staff/orders/1/status
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "status": "DELIVERING"
}
```

**주문 상태 종류:**
- `CHECKING_STOCK` - 재고 확인 중
- `RECEIVED` - 주문 접수됨
- `IN_KITCHEN` - 조리 중
- `DELIVERING` - 배달 중
- `DELIVERED` - 배달 완료

---

## 인증 방법

모든 인증이 필요한 API는 `Authorization` 헤더에 JWT 토큰을 포함해야 합니다:

```
Authorization: Bearer {accessToken}
```

Access Token이 만료되면 (기본 1시간) Refresh Token으로 새 토큰을 발급받을 수 있습니다.

---

## 초기 데이터

애플리케이션 시작 시 자동으로 다음 데이터가 생성됩니다:

### 디너 메뉴
- Valentine Dinner (35,000원)
- French Dinner (30,000원)
- English Dinner (28,000원)
- Champagne Feast Dinner (80,000원)

### 서빙 스타일
- Simple (+0원) - 플라스틱 접시와 컵, 종이 냅킨
- Grand (+5,000원) - 도자기 접시와 컵, 흰색 면 냅킨
- Deluxe (+10,000원) - 꽃병, 도자기 접시와 컵, 린넨 냅킨

### 요리
- Steak, Wine, Coffee, Salad, Egg Scramble, Bacon, Baguette, Champagne

### 테스트 계정
- **Staff 계정**
  - Email: `staff@ddoganzip.com`
  - Password: `staff123`

---

## 프로젝트 구조

```
src/main/java/com/ddoganzip/
├── auth/                    # JWT 인증
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── util/
├── customers/               # 고객 기능
│   ├── menu/               # 메뉴 조회
│   ├── cart/               # 장바구니
│   └── orders/             # 주문
├── staff/                   # 직원 기능
├── config/                  # 설정 (Security, JWT Filter)
├── entity/                  # JPA 엔티티
├── exception/               # 예외 처리
└── common/                  # 공통 유틸리티
```

---

## 라이선스

This project is for educational purposes.
