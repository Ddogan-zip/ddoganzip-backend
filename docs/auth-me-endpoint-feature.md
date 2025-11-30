# GET /api/auth/me 엔드포인트 추가에 따른 다이어그램 변경

## 개요
현재 로그인한 사용자의 프로필 정보(회원 등급 포함)를 조회하는 API 엔드포인트 추가

---

## 1. 시스템 시퀀스 다이어그램 (System Sequence Diagram)

### Before
```mermaid
sequenceDiagram
    actor User as 사용자
    participant System as 시스템

    Note over User, System: 인증 관련 시스템 이벤트

    User->>System: register(email, password, name, address, phone)
    System-->>User: 회원가입 완료

    User->>System: login(email, password)
    System-->>User: TokenResponse(accessToken, refreshToken)

    User->>System: refresh(refreshToken)
    System-->>User: TokenResponse(newAccessToken, newRefreshToken)

    User->>System: logout()
    System-->>User: 로그아웃 완료
```

### After
```mermaid
sequenceDiagram
    actor User as 사용자
    participant System as 시스템

    Note over User, System: 인증 관련 시스템 이벤트

    User->>System: register(email, password, name, address, phone)
    System-->>User: 회원가입 완료

    User->>System: login(email, password)
    System-->>User: TokenResponse(accessToken, refreshToken)

    User->>System: refresh(refreshToken)
    System-->>User: TokenResponse(newAccessToken, newRefreshToken)

    User->>System: logout()
    System-->>User: 로그아웃 완료

    rect rgb(200, 255, 200)
        Note over User, System: 🆕 신규 추가
        User->>System: getMe(accessToken)
        System-->>User: MeResponse(id, email, name, address, phone, memberGrade, orderCount)
    end
```

### 변경 요약
| 항목 | 변경 내용 |
|------|----------|
| 신규 이벤트 | `getMe(accessToken)` 추가 |
| 응답 | `MeResponse` 반환 (회원 등급 정보 포함) |

---

## 2. 시퀀스 다이어그램 (Sequence Diagram)

### Before (Auth 관련)
```mermaid
sequenceDiagram
    actor Client as 클라이언트
    participant AC as AuthController
    participant AS as AuthService
    participant AR as AuthRepository
    participant JWT as JwtUtil

    Note over Client, JWT: 로그인 플로우
    Client->>AC: POST /api/auth/login
    AC->>AS: login(LoginRequest)
    AS->>AR: findByEmail(email)
    AR-->>AS: Customer
    AS->>JWT: generateAccessToken(email, role)
    AS->>JWT: generateRefreshToken(email, role)
    AS-->>AC: TokenResponse
    AC-->>Client: TokenResponse
```

### After
```mermaid
sequenceDiagram
    actor Client as 클라이언트
    participant AC as AuthController
    participant AS as AuthService
    participant AR as AuthRepository
    participant JWT as JwtUtil

    Note over Client, JWT: 로그인 플로우
    Client->>AC: POST /api/auth/login
    AC->>AS: login(LoginRequest)
    AS->>AR: findByEmail(email)
    AR-->>AS: Customer
    AS->>JWT: generateAccessToken(email, role)
    AS->>JWT: generateRefreshToken(email, role)
    AS-->>AC: TokenResponse
    AC-->>Client: TokenResponse

    rect rgb(200, 255, 200)
        Note over Client, JWT: 🆕 프로필 조회 플로우
        Client->>AC: GET /api/auth/me (Bearer Token)
        Note right of AC: JWT에서 email 추출
        AC->>AS: getMe(email)
        AS->>AR: findByEmail(email)
        AR-->>AS: Customer
        Note right of AS: Customer에서 프로필 정보 추출<br/>(memberGrade, orderCount 포함)
        AS-->>AC: MeResponse
        AC-->>Client: MeResponse
    end
```

### 변경 요약
| 항목 | 변경 내용 |
|------|----------|
| 신규 플로우 | 프로필 조회 플로우 추가 |
| 참여 객체 | AuthController → AuthService → AuthRepository |
| 인증 방식 | JWT Bearer Token에서 email 추출 |

---

## 3. 클래스 다이어그램 (Class Diagram)

### AuthController 변경

#### Before
```mermaid
classDiagram
    class AuthController {
        -AuthService authService
        +register(RegisterRequest) ResponseEntity~ApiResponse~
        +login(LoginRequest) ResponseEntity~TokenResponse~
        +refresh(RefreshTokenRequest) ResponseEntity~TokenResponse~
        +logout() ResponseEntity~ApiResponse~
    }
```

#### After
```mermaid
classDiagram
    class AuthController {
        -AuthService authService
        +register(RegisterRequest) ResponseEntity~ApiResponse~
        +login(LoginRequest) ResponseEntity~TokenResponse~
        +refresh(RefreshTokenRequest) ResponseEntity~TokenResponse~
        +logout() ResponseEntity~ApiResponse~
        +getMe(Authentication) ResponseEntity~MeResponse~
    }

    style AuthController fill:#90EE90
```

### AuthService 변경

#### Before
```mermaid
classDiagram
    class AuthService {
        -AuthRepository authRepository
        -PasswordEncoder passwordEncoder
        -JwtUtil jwtUtil
        -AuthenticationManager authenticationManager
        +register(RegisterRequest) void
        +login(LoginRequest) TokenResponse
        +refresh(RefreshTokenRequest) TokenResponse
    }
```

#### After
```mermaid
classDiagram
    class AuthService {
        -AuthRepository authRepository
        -PasswordEncoder passwordEncoder
        -JwtUtil jwtUtil
        -AuthenticationManager authenticationManager
        +register(RegisterRequest) void
        +login(LoginRequest) TokenResponse
        +refresh(RefreshTokenRequest) TokenResponse
        +getMe(String email) MeResponse
    }

    style AuthService fill:#90EE90
```

### 신규 DTO 추가

```mermaid
classDiagram
    class MeResponse {
        <<DTO>>
        -Long id
        -String email
        -String name
        -String address
        -String phone
        -MemberGrade memberGrade
        -Integer orderCount
    }

    class MemberGrade {
        <<enumeration>>
        NORMAL
        BRONZE
        SILVER
        GOLD
        VIP
    }

    MeResponse --> MemberGrade : memberGrade

    style MeResponse fill:#90EE90
```

### 전체 Auth 클래스 다이어그램

```mermaid
classDiagram
    class AuthController {
        -AuthService authService
        +register(RegisterRequest) ResponseEntity
        +login(LoginRequest) ResponseEntity
        +refresh(RefreshTokenRequest) ResponseEntity
        +logout() ResponseEntity
        +getMe(Authentication) ResponseEntity
    }

    class AuthService {
        -AuthRepository authRepository
        -PasswordEncoder passwordEncoder
        -JwtUtil jwtUtil
        -AuthenticationManager authenticationManager
        +register(RegisterRequest) void
        +login(LoginRequest) TokenResponse
        +refresh(RefreshTokenRequest) TokenResponse
        +getMe(String) MeResponse
    }

    class AuthRepository {
        <<interface>>
        +findByEmail(String) Optional~Customer~
        +existsByEmail(String) boolean
        +save(Customer) Customer
    }

    class Customer {
        -Long id
        -String email
        -String password
        -String name
        -String address
        -String phone
        -Role role
        -MemberGrade memberGrade
        -Integer orderCount
        -Cart cart
    }

    class MeResponse {
        <<DTO - NEW>>
        -Long id
        -String email
        -String name
        -String address
        -String phone
        -MemberGrade memberGrade
        -Integer orderCount
    }

    class TokenResponse {
        <<DTO>>
        -String accessToken
        -String refreshToken
        -String tokenType
        -long expiresIn
    }

    class MemberGrade {
        <<enumeration>>
        NORMAL
        BRONZE
        SILVER
        GOLD
        VIP
    }

    AuthController --> AuthService
    AuthService --> AuthRepository
    AuthService ..> MeResponse : creates
    AuthService ..> TokenResponse : creates
    AuthRepository --> Customer
    MeResponse --> MemberGrade
    Customer --> MemberGrade

    style MeResponse fill:#90EE90
```

### 변경 요약
| 클래스 | 변경 내용 |
|--------|----------|
| AuthController | `+getMe(Authentication): ResponseEntity<MeResponse>` 메서드 추가 |
| AuthService | `+getMe(String email): MeResponse` 메서드 추가 |
| MeResponse | 🆕 신규 DTO 추가 (id, email, name, address, phone, memberGrade, orderCount) |

---

## 4. 패키지 다이어그램 (Package Diagram)

### Before
```mermaid
graph TB
    subgraph com.ddoganzip
        subgraph auth
            auth_controller[controller]
            auth_service[service]
            auth_repository[repository]
            auth_entity[entity]
            auth_dto[dto]
            auth_util[util]
            auth_config[config]
        end
    end
```

### After
```mermaid
graph TB
    subgraph com.ddoganzip
        subgraph auth
            auth_controller[controller]
            auth_service[service]
            auth_repository[repository]
            auth_entity[entity]
            auth_dto[dto ✨]
            auth_util[util]
            auth_config[config]
        end
    end

    style auth_dto fill:#90EE90
```

### auth.dto 패키지 상세

#### Before
```
com.ddoganzip.auth.dto
├── LoginRequest.java
├── RegisterRequest.java
├── RefreshTokenRequest.java
└── TokenResponse.java
```

#### After
```
com.ddoganzip.auth.dto
├── LoginRequest.java
├── RegisterRequest.java
├── RefreshTokenRequest.java
├── TokenResponse.java
└── MeResponse.java        ← 🆕 신규 추가
```

### 변경 요약
| 패키지 | 변경 내용 |
|--------|----------|
| com.ddoganzip.auth.dto | `MeResponse.java` 추가 |

---

## 5. API 명세

### Request
| 항목 | 값 |
|------|-----|
| Method | GET |
| URL | /api/auth/me |
| Headers | Authorization: Bearer {accessToken} |
| Body | 없음 |

### Response
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "address": "서울시 강남구",
  "phone": "010-1234-5678",
  "memberGrade": "SILVER",
  "orderCount": 12
}
```

### Response 필드 설명
| 필드 | 타입 | 설명 |
|------|------|------|
| id | number | 사용자 ID |
| email | string | 이메일 |
| name | string | 이름 |
| address | string | 주소 |
| phone | string | 전화번호 |
| memberGrade | string | 회원 등급 (NORMAL, BRONZE, SILVER, GOLD, VIP) |
| orderCount | number | 누적 주문 횟수 |

---

## 6. 변경 파일 목록

| 파일 | 변경 유형 | 설명 |
|------|----------|------|
| `MeResponse.java` | 🆕 신규 | 프로필 응답 DTO |
| `AuthService.java` | 수정 | `getMe()` 메서드 추가 |
| `AuthController.java` | 수정 | `GET /api/auth/me` 엔드포인트 추가 |

---

## 7. 관련 기능과의 연계

```mermaid
flowchart LR
    subgraph Frontend
        A[로그인 화면] --> B[메인 화면]
        B --> C[마이페이지]
    end

    subgraph Backend API
        D[POST /api/auth/login]
        E[GET /api/auth/me]
    end

    A -->|로그인 요청| D
    D -->|TokenResponse| B
    C -->|프로필 조회| E
    E -->|MeResponse<br/>회원등급 포함| C

    style E fill:#90EE90
```

회원 등급 할인 기능과 연계하여, 프론트엔드에서 사용자의 현재 등급과 주문 횟수를 표시할 수 있습니다.
