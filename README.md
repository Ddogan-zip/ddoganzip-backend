# Ddoganzip - 미스터 대박 디너 서비스 백엔드

훌륭한 디너 만찬을 고객의 집으로 배달하는 서비스의 백엔드 API 서버입니다.

음성 인식 기술을 활용하여 사용자가 말로 간편하게 주문할 수 있으며, 직원은 실시간으로 주문을 확인하고 관리할 수 있습니다.

## 🔗 관련 프로젝트

- **프론트엔드**: [ddoganzip-frontend](https://github.com/Ddogan-zip/ddoganzip-frontend) (React + TypeScript + Vite)
- **백엔드**: 현재 레포지토리 (Spring Boot + PostgreSQL)

## 📚 API 문서

Swagger UI를 통한 상세 API 명세: https://ddogan-zip.github.io/ddoganzip-backend/

## 🏗 시스템 아키텍처

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   Frontend      │      │   Backend       │      │   Database      │
│   (React)       │─────▶│   (Spring)      │─────▶│  (PostgreSQL)   │
│   Port: 5173    │ HTTP │   Port: 8080    │ JDBC │   Port: 5432    │
└─────────────────┘      └─────────────────┘      └─────────────────┘
        │                         │
        │                         │
        ▼                         ▼
  음성 인식 API            JWT 인증 + CORS
  (Web Speech API)        (Spring Security)
```

## 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: PostgreSQL / H2 (개발용)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Documentation**: OpenAPI 3.0 (Swagger UI)

## 🚀 빠른 시작 (프론트엔드와 함께)

전체 시스템을 실행하려면 백엔드와 프론트엔드를 모두 실행해야 합니다.

### 1단계: 백엔드 실행 (이 레포지토리)

```bash
# H2 인메모리 DB로 빠르게 시작 (DB 설치 불필요)
./gradlew bootRun --args='--spring.profiles.active=h2'

# 또는 PostgreSQL 사용 (설정 필요)
./gradlew bootRun
```

백엔드 서버: http://localhost:8080

### 2단계: 프론트엔드 실행

```bash
# 프론트엔드 레포지토리 클론
git clone https://github.com/Ddogan-zip/ddoganzip-frontend.git
cd ddoganzip-frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드 서버: http://localhost:5173

### 3단계: 접속

- **메인 페이지**: http://localhost:5173
- **음성 주문**: http://localhost:5173/order
- **직원 대시보드**: http://localhost:5173/staff
- **API 문서**: http://localhost:8080/swagger-ui.html

### CORS 설정

백엔드는 다음 Origin을 허용하도록 설정되어 있습니다:
- `http://localhost:3000` (React 기본 포트)
- `http://localhost:5173` (Vite 기본 포트)
- `http://localhost:5000` (AI 서비스)

---

## 실행 방법 (백엔드만)

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

## 🔌 프론트엔드 연동 가이드

### API 베이스 URL 설정

프론트엔드에서 `.env` 파일에 다음 환경 변수를 설정하세요:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

### JWT 토큰 관리

프론트엔드는 다음과 같이 JWT 토큰을 관리합니다:

1. **로그인 시**: Access Token과 Refresh Token을 LocalStorage에 저장
2. **API 요청 시**: Axios 인터셉터가 자동으로 `Authorization` 헤더에 토큰 추가
3. **토큰 만료 시**: 401 에러 발생 시 Refresh Token으로 자동 갱신

```typescript
// 프론트엔드 Axios 설정 예시
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 음성 주문 플로우

1. 사용자가 음성으로 주문 (예: "발렌타인 디너 2개")
2. 프론트엔드가 음성을 텍스트로 변환 (Web Speech API)
3. AI 서비스가 주문 의도를 파싱
4. 프론트엔드가 `POST /api/cart/items`로 장바구니에 추가
5. 사용자 확인 후 `POST /api/orders/checkout`으로 주문 완료

### 직원 대시보드 실시간 업데이트

직원 대시보드는 5초마다 자동으로 활성 주문을 조회합니다:

```typescript
// React Query를 사용한 자동 새로고침
useQuery({
  queryKey: ['active-orders'],
  queryFn: getActiveOrders,
  refetchInterval: 5000, // 5초마다 자동 새로고침
});
```

### 에러 처리

백엔드는 다음과 같은 에러 응답 형식을 반환합니다:

```json
{
  "success": false,
  "message": "Error message",
  "errors": ["Validation error 1", "Validation error 2"]
}
```

프론트엔드는 이를 파싱하여 사용자에게 토스트 알림으로 표시합니다.

### 주요 API 연동 예시

#### 1. 메뉴 조회 및 표시
```typescript
const { data: menuList } = useQuery({
  queryKey: ['menu-list'],
  queryFn: () => axios.get('/api/menu/list'),
});
```

#### 2. 장바구니에 추가
```typescript
const addToCartMutation = useMutation({
  mutationFn: (item) => axios.post('/api/cart/items', item),
  onSuccess: () => {
    queryClient.invalidateQueries(['cart']);
    toast.success('장바구니에 추가되었습니다');
  },
});
```

#### 3. 주문하기
```typescript
const checkoutMutation = useMutation({
  mutationFn: (data) => axios.post('/api/orders/checkout', data),
  onSuccess: () => {
    toast.success('주문이 완료되었습니다');
    navigate('/orders/history');
  },
});
```

---

## 🔧 개발 가이드

### 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests AuthServiceTest
```

### 빌드

```bash
# JAR 파일 생성
./gradlew build

# 빌드 파일 위치
# build/libs/ddoganzip-0.0.1-SNAPSHOT.jar
```

### 운영 환경 배포

```bash
# 프로필 지정하여 실행
java -jar build/libs/ddoganzip-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 📝 라이선스

This project is for educational purposes.
