-- Serving Styles
-- Simple: 플라스틱 접시, 플라스틱 컵, 종이 냅킨, 플라스틱 쟁반
-- Grand: 도자기 접시, 도자기 컵, 흰색 면 냅킨, 나무 쟁반
-- Deluxe: 꽃병, 도자기 접시, 도자기 컵, 린넨 냅킨, 나무 쟁반, 유리 와인잔
INSERT INTO serving_styles (name, additional_price, description) VALUES
('Simple', 0, '플라스틱 접시와 플라스틱 컵, 종이 냅킨이 플라스틱 쟁반에 제공'),
('Grand', 15000, '도자기 접시와 도자기 컵, 흰색 면 냅킨이 나무 쟁반에 제공'),
('Deluxe', 30000, '꽃병, 도자기 접시와 도자기 컵, 린넨 냅킨이 나무 쟁반에 제공');

-- Dishes (with inventory: current_stock, minimum_stock, category)
-- Category: GENERAL (일반 재고, 화/금 충전), LIQUOR (주류, 매일 아침 충전), DECORATION (장식품, 재고 관리 제외)
INSERT INTO dishes (name, description, base_price, default_quantity, current_stock, minimum_stock, category) VALUES
('Steak', '프리미엄 스테이크', 25000, 1, 5, 10, 'GENERAL'),
('Wine', '레드 와인', 8000, 1, 15, 20, 'LIQUOR'),
('Coffee', '아메리카노', 3000, 1, 50, 30, 'GENERAL'),
('Salad', '신선한 샐러드', 5000, 1, 8, 15, 'GENERAL'),
('Scrambled Eggs', '에그 스크램블', 4000, 1, 25, 20, 'GENERAL'),
('Bacon', '베이컨', 5000, 1, 12, 15, 'GENERAL'),
('Bread', '식빵', 2000, 1, 30, 25, 'GENERAL'),
('Champagne', '샴페인 (병)', 50000, 1, 3, 5, 'LIQUOR'),
('Baguette', '바게트빵', 3000, 1, 20, 15, 'GENERAL'),
('Coffee Pot', '커피 포트 (6잔)', 10000, 1, 6, 8, 'GENERAL'),
('Heart Decoration', '하트 장식', 2000, 1, 0, 0, 'DECORATION'),
('Cupid Decoration', '큐피드 장식', 3000, 1, 0, 0, 'DECORATION'),
('Napkin', '냅킨', 500, 1, 100, 50, 'GENERAL');

-- Staff Availability (직원 가용성)
-- 총 10명: 요리 담당 5명, 배달 담당 5명
INSERT INTO staff_availability (id, available_cooks, total_cooks, available_drivers, total_drivers) VALUES
(1, 5, 5, 5, 5);

-- Dinners
INSERT INTO dinners (name, description, base_price, image_url) VALUES
('Valentine Dinner', '작은 하트 모양과 큐피드가 장식된 접시에 냅킨과 함께 와인과 스테이크가 제공', 45000, 'https://example.com/valentine.jpg'),
('French Dinner', '커피 한잔, 와인 한잔, 샐러드, 스테이크 제공', 48000, 'https://example.com/french.jpg'),
('English Dinner', '에그 스크램블, 베이컨, 빵, 스테이크가 제공', 42000, 'https://example.com/english.jpg'),
('Champagne Feast Dinner', '항상 2인 식사이고, 샴페인 1병, 4개의 바게트빵, 커피 포트, 와인, 스테이크 제공', 120000, 'https://example.com/champagne.jpg');

-- Dinner-Dish relationships with quantities
-- Valentine Dinner (id=1): Steak(1)x1, Wine(2)x1, Heart Decoration(11)x1, Cupid Decoration(12)x1, Napkin(13)x1
INSERT INTO dinner_dishes (dinner_id, dish_id, quantity) VALUES
(1, 1, 1), (1, 2, 1), (1, 11, 1), (1, 12, 1), (1, 13, 1);

-- French Dinner (id=2): Coffee(3)x1, Wine(2)x1, Salad(4)x1, Steak(1)x1
INSERT INTO dinner_dishes (dinner_id, dish_id, quantity) VALUES
(2, 3, 1), (2, 2, 1), (2, 4, 1), (2, 1, 1);

-- English Dinner (id=3): Scrambled Eggs(5)x1, Bacon(6)x1, Bread(7)x1, Steak(1)x1
INSERT INTO dinner_dishes (dinner_id, dish_id, quantity) VALUES
(3, 5, 1), (3, 6, 1), (3, 7, 1), (3, 1, 1);

-- Champagne Feast Dinner (id=4): Champagne(8)x1, Baguette(9)x4, Coffee Pot(10)x1, Wine(2)x1, Steak(1)x2
INSERT INTO dinner_dishes (dinner_id, dish_id, quantity) VALUES
(4, 8, 1), (4, 9, 4), (4, 10, 1), (4, 2, 1), (4, 1, 2);

-- Dinner-ServingStyle relationships
-- Valentine, French, English dinners: 모든 스타일 가능
INSERT INTO dinner_serving_styles (dinner_id, style_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3);

-- Champagne Feast Dinner: Grand(2), Deluxe(3) 스타일만 가능
INSERT INTO dinner_serving_styles (dinner_id, style_id) VALUES
(4, 2), (4, 3);

-- Test users
-- User accounts (password: test1234) - BCrypt hash generated and verified
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('user@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '테스트 사용자', '서울시 강남구 테헤란로 123', '010-1234-5678', 'USER', NOW(), NOW()),
('john@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John Smith', '서울시 서초구 강남대로 456', '010-2222-3333', 'USER', NOW(), NOW()),
('emily@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emily Johnson', '서울시 송파구 올림픽로 789', '010-3333-4444', 'USER', NOW(), NOW()),
('mike@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mike Brown', '서울시 강동구 천호대로 321', '010-4444-5555', 'USER', NOW(), NOW());

-- Staff account (password: staff1234) - BCrypt hash generated and verified
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('staff@test.com', '$2a$10$Xl4/5XJZLxJjCXXvlAl1/.Qp0K3CJM/W0.yLqg.qH0aDHEOZGTnTu', '직원 계정', '서울시 강남구 테헤란로 456', '010-9876-5432', 'STAFF', NOW(), NOW());

-- Carts for users
INSERT INTO carts (customer_id) VALUES
(1),  -- user@test.com의 장바구니
(2),  -- john@test.com의 장바구니
(3);  -- emily@test.com의 장바구니

-- Cart Items (샘플 장바구니 데이터)
-- user@test.com의 장바구니: Valentine Dinner x1 (Deluxe 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(1, 1, 3, 1);

-- john@test.com의 장바구니: French Dinner x1 (Grand 스타일), English Dinner x2 (Simple 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(2, 2, 2, 1),
(2, 3, 1, 2);

-- emily@test.com의 장바구니: Champagne Feast Dinner x1 (Deluxe 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(3, 4, 3, 1);

-- Sample Orders (샘플 주문 데이터)
-- mike@test.com의 완료된 주문 (Valentine Dinner Deluxe)
INSERT INTO orders (customer_id, status, order_date, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(4, 'DELIVERED', TIMESTAMP '2025-11-16 15:30:00', '서울시 강동구 천호대로 321', TIMESTAMP '2025-11-17 19:00:00', 75000, TIMESTAMP '2025-11-16 15:30:00', TIMESTAMP '2025-11-17 19:30:00');

-- john@test.com의 배달 중인 주문 (French Dinner Grand x2)
INSERT INTO orders (customer_id, status, order_date, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(2, 'DELIVERING', TIMESTAMP '2025-11-18 14:00:00', '서울시 서초구 강남대로 456', TIMESTAMP '2025-11-18 18:00:00', 126000, TIMESTAMP '2025-11-18 14:00:00', TIMESTAMP '2025-11-18 16:30:00');

-- emily@test.com의 조리 중인 주문 (Champagne Feast Dinner Grand)
INSERT INTO orders (customer_id, status, order_date, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(3, 'IN_KITCHEN', TIMESTAMP '2025-11-18 16:00:00', '서울시 송파구 올림픽로 789', TIMESTAMP '2025-11-18 20:00:00', 135000, TIMESTAMP '2025-11-18 16:00:00', TIMESTAMP '2025-11-18 16:15:00');

-- user@test.com의 접수된 주문 (English Dinner Simple x2)
INSERT INTO orders (customer_id, status, order_date, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(1, 'RECEIVED', TIMESTAMP '2025-11-18 17:00:00', '서울시 강남구 테헤란로 123', TIMESTAMP '2025-11-18 19:30:00', 84000, TIMESTAMP '2025-11-18 17:00:00', TIMESTAMP '2025-11-18 17:05:00');

-- john@test.com의 재고 확인 중인 주문 (Champagne Feast Dinner Deluxe)
INSERT INTO orders (customer_id, status, order_date, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(2, 'CHECKING_STOCK', TIMESTAMP '2025-11-18 17:30:00', '서울시 서초구 강남대로 456', TIMESTAMP '2025-11-19 12:00:00', 150000, TIMESTAMP '2025-11-18 17:30:00', TIMESTAMP '2025-11-18 17:30:00');

-- Order Items (주문 아이템 데이터)
-- 주문 1 (mike, DELIVERED): Valentine Dinner x1 (Deluxe 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(1, 1, 3, 1, 75000);

-- 주문 2 (john, DELIVERING): French Dinner x2 (Grand 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(2, 2, 2, 2, 126000);

-- 주문 3 (emily, IN_KITCHEN): Champagne Feast Dinner x1 (Grand 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(3, 4, 2, 1, 135000);

-- 주문 4 (user, RECEIVED): English Dinner x2 (Simple 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(4, 3, 1, 2, 84000);

-- 주문 5 (john, CHECKING_STOCK): Champagne Feast Dinner x1 (Deluxe 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(5, 4, 3, 1, 150000);
