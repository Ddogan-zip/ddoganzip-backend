# DdoganZip 시스템 시퀀스 다이어그램

## 1. Register (회원가입)

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 회원가입 요청 (이메일, 비밀번호, 이름, 주소, 전화번호)
    System-->>User: 회원가입 완료 응답 (성공/실패 메시지)
```

## 2. Login (로그인)

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 로그인 요청 (이메일, 비밀번호)
    System-->>User: 인증 토큰 발급 (Access Token, Refresh Token)
```

## 3. View Menu (메뉴 조회)

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 메뉴 목록 조회 요청
    System-->>User: 디너 목록 반환

    User->>System: 메뉴 상세 조회 요청 (디너 ID)
    System-->>User: 디너 상세 정보 반환 (구성 요리, 서빙 스타일, 가격)
```

## 4. Manage Cart (장바구니 관리)

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 장바구니 조회 요청
    System-->>User: 장바구니 내역 반환

    User->>System: 상품 추가 요청 (디너 ID, 서빙 스타일, 수량)
    System-->>User: 추가 완료 응답

    User->>System: 수량 변경 요청 (아이템 ID, 새 수량)
    System-->>User: 변경 완료 응답

    User->>System: 옵션 수정 요청 (아이템 ID, 서빙 스타일)
    System-->>User: 수정 완료 응답

    User->>System: 커스터마이징 요청 (아이템 ID, 추가/제거 요리)
    System-->>User: 커스터마이징 완료 응답

    User->>System: 상품 삭제 요청 (아이템 ID)
    System-->>User: 삭제 완료 응답
```

## 5. Checkout (주문하기)

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 결제 요청 (배달 주소, 배달 날짜)
    System-->>User: 주문 완료 응답 (주문 ID, 주문 정보)
```

## 6. View Order (주문 조회)

### 6-1. 일반 사용자 - 주문 내역 조회

```mermaid
sequenceDiagram
    actor User
    participant System

    User->>System: 주문 내역 조회 요청
    System-->>User: 본인 주문 목록 반환

    User->>System: 주문 상세 조회 요청 (주문 ID)
    System-->>User: 주문 상세 정보 반환 (상태, 구성품, 가격)
```

### 6-2. 스태프 - 활성 주문 조회

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: 활성 주문 목록 조회 요청
    System-->>User: 모든 활성 주문 목록 반환 (DRIVER_RETURNED 제외)
```

## 7. Manage Status (주문 상태 관리 - 스태프)

### 7-1. 주문 상태 변경

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: 주문 상태 변경 요청 (주문 ID, 새 상태)
    System-->>Staff: 상태 변경 완료 응답
```

### 7-2. 재고 확인

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: 재고 현황 조회 요청
    System-->>Staff: 전체 재고 목록 반환

    Staff->>System: 주문 재고 확인 요청 (주문 ID)
    System-->>Staff: 주문 이행 가능 여부 반환 (부족 재고 정보)
```

### 7-3. 배달 직원 복귀 처리

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: 배달 직원 복귀 처리 요청 (주문 ID)
    System-->>Staff: 복귀 처리 완료 응답 (배달원 가용 상태 복구)
```

### 7-4. 스태프 가용 현황 조회

```mermaid
sequenceDiagram
    actor Staff
    participant System

    Staff->>System: 스태프 가용 현황 조회 요청
    System-->>Staff: 조리사/배달원 가용 인원 반환
```
