# 프론트엔드 연동을 위한 백엔드 API 명세

안녕하세요! 똑간집 배달 서비스 백엔드가 완성되었습니다.
이 문서는 실제 구현된 API 명세입니다. 프론트엔드를 이 명세에 맞춰 개발해주세요.

## 📋 기본 설정

### 서버 정보
- **Base URL**: `http://localhost:8080`
- **서버 포트**: 8080

### CORS 설정
백엔드에서 허용하는 Origin:
```javascript
const allowedOrigins = [
  "http://localhost:5176",  // ✅ Vite 기본 포트 지원
  "http://localhost:3000",
  "http://localhost:5000"
];

const allowedHeaders = ["Content-Type", "Authorization"];
const allowedMethods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"];
const credentials = true;
```

### 인증 헤더
```
Authorization: Bearer {accessToken}
```

### JWT 토큰
- **Access Token 만료**: 1시간 (3600초)
- **Refresh Token 만료**: 7일

---

## 🔐 1. 인증 API (/api/auth)

### POST /api/auth/register
회원가입

**요청:**
```typescript
interface RegisterRequest {
  email: string;        // 필수, 이메일 형식
  password: string;     // 필수, 최소 6자
  name: string;         // 필수
  address?: string;     // 선택
  phone?: string;       // 선택
}
```

**응답 (200):**
```typescript
interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;    // "Bearer"
  expiresIn: number;    // 3600 (1시간)
}
```

**실제 응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**에러 응답 (400):**
```typescript
interface ErrorResponse {
  error: {
    code: string;
    message: string;
    details?: string;
  }
}
```

**실제 에러 예시:**
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력 데이터가 유효하지 않습니다",
    "details": "Email is required, Password must be at least 6 characters"
  }
}
```

---

### POST /api/auth/login
로그인

**요청:**
```typescript
interface LoginRequest {
  email: string;
  password: string;
}
```

**응답 (200):**
```typescript
interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}
```

**에러 응답 (401):**
```json
{
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이메일 또는 비밀번호가 일치하지 않습니다"
  }
}
```

---

### POST /api/auth/refresh
토큰 갱신

**요청:**
```typescript
interface RefreshTokenRequest {
  refreshToken: string;
}
```

**응답 (200):**
```typescript
interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}
```

---

### POST /api/auth/logout
로그아웃 (인증 필요)

**헤더:**
```
Authorization: Bearer {accessToken}
```

**응답 (200):**
```json
{
  "message": "로그아웃 성공"
}
```

---

## 🍽️ 2. 메뉴 API (/api/menu)

### GET /api/menu/list
모든 디너 메뉴 목록 조회 (인증 불필요)

**응답 (200):**
```typescript
type MenuListResponse = MenuListItem[];

interface MenuListItem {
  id: number;
  name: string;
  description: string;
  basePrice: number;     // ✅ number 타입 (Integer)
  imageUrl: string;      // ✅ 이미지 URL 포함
}
```

**실제 응답 예시:**
```json
[
  {
    "id": 1,
    "name": "프리미엄 스테이크 디너",
    "description": "최상급 한우 스테이크와 사이드 메뉴",
    "basePrice": 45000,
    "imageUrl": "https://example.com/steak.jpg"
  },
  {
    "id": 2,
    "name": "시푸드 파스타 세트",
    "description": "신선한 해산물이 가득한 파스타",
    "basePrice": 32000,
    "imageUrl": "https://example.com/pasta.jpg"
  }
]
```

---

### GET /api/menu/details/:dinnerId
특정 디너 메뉴 상세 정보 조회 (인증 불필요)

**URL 파라미터:**
- `dinnerId`: 메뉴 ID (숫자)

**응답 (200):**
```typescript
interface MenuDetailResponse {
  id: number;
  name: string;
  description: string;
  basePrice: number;
  imageUrl: string;
  dishes: DishInfo[];
  availableStyles: ServingStyleInfo[];
}

interface DishInfo {
  id: number;
  name: string;
  description: string;    // ✅ 디스크 설명 포함
  basePrice: number;      // ✅ 디스크 가격 포함
}

interface ServingStyleInfo {
  id: number;
  name: string;
  additionalPrice: number;
  description: string;
}
```

**실제 응답 예시:**
```json
{
  "id": 1,
  "name": "프리미엄 스테이크 디너",
  "description": "최상급 한우 스테이크와 사이드 메뉴",
  "basePrice": 45000,
  "imageUrl": "https://example.com/steak.jpg",
  "dishes": [
    {
      "id": 1,
      "name": "한우 안심 스테이크",
      "description": "200g 프리미엄 안심",
      "basePrice": 35000
    },
    {
      "id": 2,
      "name": "그릴드 야채",
      "description": "신선한 계절 야채",
      "basePrice": 5000
    },
    {
      "id": 3,
      "name": "마늘빵",
      "description": "수제 마늘빵",
      "basePrice": 3000
    }
  ],
  "availableStyles": [
    {
      "id": 1,
      "name": "심플",
      "additionalPrice": 0,
      "description": "기본 구성"
    },
    {
      "id": 2,
      "name": "프리미엄",
      "additionalPrice": 10000,
      "description": "와인과 디저트 포함"
    },
    {
      "id": 3,
      "name": "패밀리",
      "additionalPrice": 15000,
      "description": "2인분 + 사이드 메뉴 추가"
    }
  ]
}
```

---

## 🛒 3. 장바구니 API (/api/cart) - 모두 인증 필요

### GET /api/cart
현재 사용자의 장바구니 조회

**헤더:**
```
Authorization: Bearer {accessToken}
```

**응답 (200):**
```typescript
interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  totalPrice: number;
}

interface CartItemResponse {
  id: number;              // itemId
  dinnerId: number;
  dinnerName: string;
  servingStyleId: number;
  servingStyleName: string;
  quantity: number;
  customizations: CustomizationResponse[];
  unitPrice: number;       // 개당 가격
  totalPrice: number;      // quantity * unitPrice
}

interface CustomizationResponse {
  action: string;       // "ADD", "REMOVE", "REPLACE"
  dishId: number;
  quantity: number;
}
```

---

### POST /api/cart/items
장바구니에 상품 추가

**헤더:**
```
Authorization: Bearer {accessToken}
```

**요청:**
```typescript
interface AddToCartRequest {
  dinnerId: number;           // 필수
  servingStyleId: number;     // 필수
  quantity: number;           // 기본값: 1, 최소값: 1
  customizations?: CustomizationRequest[];
}

interface CustomizationRequest {
  action: string;      // "ADD", "REMOVE", "REPLACE"
  dishId: number;
  quantity: number;
}
```

**응답 (200):**
```typescript
interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  totalPrice: number;
}
```

**✅ 중요**: 이 API는 **CartResponse를 직접 반환**합니다. 재조회 불필요!

---

### PUT /api/cart/items/:itemId/quantity
장바구니 상품 수량 변경

**URL 파라미터:**
- `itemId`: 장바구니 아이템 ID

**요청:**
```typescript
interface UpdateQuantityRequest {
  quantity: number;
}
```

**응답 (200):**
```typescript
interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  totalPrice: number;
}
```

**✅ 중요**: CartResponse를 직접 반환합니다!

---

### PUT /api/cart/items/:itemId/options
장바구니 상품 옵션 변경

**요청:**
```typescript
interface UpdateOptionsRequest {
  servingStyleId?: number;
  customizations?: CustomizationRequest[];
}
```

**응답 (200):**
```typescript
interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  totalPrice: number;
}
```

**✅ 중요**: CartResponse를 직접 반환합니다!

---

### DELETE /api/cart/items/:itemId
장바구니에서 상품 삭제

**응답 (200):**
```typescript
interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  totalPrice: number;
}
```

**✅ 중요**: CartResponse를 직접 반환합니다!

---

## 📦 4. 주문 API (/api/orders) - 모두 인증 필요

### POST /api/orders/checkout
장바구니의 모든 상품을 주문으로 전환

**요청:**
```typescript
interface CheckoutRequest {
  deliveryAddress: string;    // 필수
  deliveryDate?: string;      // ISO 8601 형식 (선택)
}
```

**요청 예시:**
```json
{
  "deliveryAddress": "서울시 강남구 테헤란로 123",
  "deliveryDate": "2025-11-20T18:00:00"
}
```

**응답 (200):**
```typescript
interface OrderDetailResponse {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  status: OrderStatus;
  deliveryAddress: string;
  deliveryDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}

interface OrderItemResponse {
  dinnerId: number;
  dinnerName: string;
  servingStyleId: number;
  servingStyleName: string;
  quantity: number;
  customizations: CustomizationResponse[];
  unitPrice: number;
  totalPrice: number;
}

type OrderStatus =
  | "CHECKING_STOCK"
  | "RECEIVED"
  | "IN_KITCHEN"
  | "DELIVERING"
  | "DELIVERED";
```

---

### GET /api/orders/history
현재 사용자의 모든 주문 내역 조회

**응답 (200):**
```typescript
interface OrderHistoryResponse {
  orders: OrderHistoryItem[];
}

interface OrderHistoryItem {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  status: OrderStatus;
  deliveryAddress: string;
  deliveryDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}
```

---

### GET /api/orders/:orderId
특정 주문의 상세 정보 조회

**응답 (200):**
```typescript
interface OrderDetailResponse {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  status: OrderStatus;
  deliveryAddress: string;
  deliveryDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 👨‍💼 5. 직원용 API (/api/staff) - STAFF 권한 필요

### GET /api/staff/orders/active
배달 완료되지 않은 모든 주문 조회

**헤더:**
```
Authorization: Bearer {accessToken}
```

**권한**: STAFF 역할 필요

**응답 (200):**
```typescript
interface ActiveOrdersResponse {
  orders: ActiveOrder[];
}

interface ActiveOrder {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  status: OrderStatus;
  deliveryAddress: string;
  deliveryDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}
```

---

### PUT /api/staff/orders/:orderId/status
주문 상태 변경

**요청:**
```typescript
interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

type OrderStatus =
  | "CHECKING_STOCK"    // 재고 확인 중
  | "RECEIVED"          // 주문 접수
  | "IN_KITCHEN"        // 조리 중
  | "DELIVERING"        // 배달 중
  | "DELIVERED";        // 배달 완료
```

**응답 (200):**
```typescript
interface OrderDetailResponse {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  status: OrderStatus;
  deliveryAddress: string;
  deliveryDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 🎯 역할(Role) 시스템

```typescript
type Role = "USER" | "STAFF";
```

**✅ 중요**: "USER" 역할 사용 (CUSTOMER 아님)

JWT 토큰 payload:
```typescript
interface JWTPayload {
  userId: number;
  email: string;
  role: "USER" | "STAFF";
  exp: number;
}
```

---

## 🧪 테스트 데이터

### 테스트 계정

**일반 사용자:**
- 이메일: `user@test.com`
- 비밀번호: `test1234`
- 역할: USER

**직원:**
- 이메일: `staff@test.com`
- 비밀번호: `staff1234`
- 역할: STAFF

### 메뉴 데이터 (5개)

1. **프리미엄 스테이크 디너** (45,000원)
   - 한우 안심 스테이크 (35,000원)
   - 그릴드 야채 (5,000원)
   - 마늘빵 (3,000원)

2. **시푸드 파스타 세트** (32,000원)
   - 새우 파스타 (18,000원)
   - 시저 샐러드 (7,000원)
   - 마늘빵 (3,000원)

3. **한우 갈비 정식** (55,000원)
   - 한우 갈비 (38,000원)
   - 된장찌개 (5,000원)
   - 그릴드 야채 (5,000원)

4. **삼겹살 구이 세트** (28,000원)
   - 삼겹살 (15,000원)
   - 상추쌈 (3,000원)
   - 된장찌개 (5,000원)

5. **연어 스시 모듬** (38,000원)
   - 연어 스시 (20,000원)
   - 참치 스시 (18,000원)

### 서빙 스타일 (3개)

1. **심플** (+0원): 기본 구성
2. **프리미엄** (+10,000원): 와인과 디저트 포함
3. **패밀리** (+15,000원): 2인분 + 사이드 메뉴 추가

---

## ⚠️ 에러 응답 형식

모든 에러는 다음 형식으로 통일되어 있습니다:

```typescript
interface ErrorResponse {
  error: {
    code: string;
    message: string;
    details?: string;
  }
}
```

### 에러 코드 목록

- `VALIDATION_ERROR`: 입력 데이터 검증 실패
- `INVALID_CREDENTIALS`: 로그인 실패
- `CUSTOM_ERROR`: 비즈니스 로직 에러
- `INTERNAL_SERVER_ERROR`: 서버 내부 오류

### 에러 응답 예시

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력 데이터가 유효하지 않습니다",
    "details": "Email is required, Password must be at least 6 characters"
  }
}
```

```json
{
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이메일 또는 비밀번호가 일치하지 않습니다"
  }
}
```

---

## 📝 프론트엔드 개발 체크리스트

- [ ] API Base URL을 `http://localhost:8080`으로 설정
- [ ] Vite 개발 서버를 포트 5176으로 실행 (기본값 사용)
- [ ] "USER", "STAFF" 역할 사용
- [ ] Cart 수정 API가 CartResponse를 반환하므로 재조회 불필요
- [ ] 에러 응답을 `{ error: { code, message, details } }` 형식으로 처리
- [ ] MenuListResponse에 imageUrl 필드 사용
- [ ] DishInfo에 description, basePrice 필드 사용
- [ ] OrderStatus enum 값 확인 (CHECKING_STOCK, RECEIVED, IN_KITCHEN, DELIVERING, DELIVERED)
- [ ] JWT 토큰 만료 시간 처리 (Access: 1시간, Refresh: 7일)
- [ ] 모든 가격은 number 타입으로 처리

---

## 🚀 실행 방법

### 백엔드 실행
```bash
cd ddoganzip-backend
./gradlew bootRun
```
서버: http://localhost:8080

### 프론트엔드 실행
```bash
cd ddoganzip-frontend
npm install
npm run dev
```
기본 포트 5176이 CORS에 허용되어 있습니다.

---

## 📚 API 문서

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **GitHub Pages**: https://ddogan-zip.github.io/ddoganzip-backend/

---

## ✨ 주요 기능 정리

### ✅ 완벽하게 구현된 기능

1. **JWT 인증**
   - Access Token (1시간)
   - Refresh Token (7일)
   - 자동 갱신 지원

2. **역할 기반 접근 제어**
   - USER: 일반 사용자
   - STAFF: 직원

3. **장바구니 시스템**
   - 실시간 CartResponse 반환
   - 커스터마이징 지원 (ADD, REMOVE, REPLACE)

4. **주문 시스템**
   - 5단계 주문 상태 관리
   - 배송 주소 및 날짜 지정

5. **직원 대시보드**
   - 활성 주문 조회
   - 주문 상태 변경

---

질문이나 추가 수정이 필요하면 언제든 문의해주세요!
