-- Serving Styles
INSERT INTO serving_styles (name, additional_price, description) VALUES
('심플', 0, '기본 구성'),
('프리미엄', 10000, '와인과 디저트 포함'),
('패밀리', 15000, '2인분 + 사이드 메뉴 추가');

-- Dishes
INSERT INTO dishes (name, description, base_price, default_quantity) VALUES
('한우 안심 스테이크', '200g 프리미엄 안심', 35000, 1),
('그릴드 야채', '신선한 계절 야채', 5000, 1),
('마늘빵', '수제 마늘빵', 3000, 1),
('새우 파스타', '통새우가 들어간 토마토 파스타', 18000, 1),
('시저 샐러드', '신선한 로메인 상추', 7000, 1),
('한우 갈비', '300g 프리미엄 갈비', 38000, 1),
('된장찌개', '전통 방식 된장찌개', 5000, 1),
('삼겹살', '국내산 삼겹살 200g', 15000, 1),
('상추쌈', '신선한 쌈 채소', 3000, 1),
('연어 스시', '노르웨이산 연어 8피스', 20000, 1),
('참치 스시', '참치 뱃살 6피스', 18000, 1);

-- Dinners
INSERT INTO dinners (name, description, base_price, image_url) VALUES
('프리미엄 스테이크 디너', '최상급 한우 스테이크와 사이드 메뉴', 45000, 'https://example.com/steak.jpg'),
('시푸드 파스타 세트', '신선한 해산물이 가득한 파스타', 32000, 'https://example.com/pasta.jpg'),
('한우 갈비 정식', '부드러운 한우 갈비와 전통 반찬', 55000, 'https://example.com/galbi.jpg'),
('삼겹살 구이 세트', '국내산 삼겹살과 신선한 야채', 28000, 'https://example.com/samgyeopsal.jpg'),
('연어 스시 모듬', '신선한 연어와 참치 스시', 38000, 'https://example.com/sushi.jpg');

-- Dinner-Dish relationships
-- 프리미엄 스테이크 디너 (id=1): 한우 안심 스테이크(1), 그릴드 야채(2), 마늘빵(3)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(1, 1), (1, 2), (1, 3);

-- 시푸드 파스타 세트 (id=2): 새우 파스타(4), 시저 샐러드(5), 마늘빵(3)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(2, 4), (2, 5), (2, 3);

-- 한우 갈비 정식 (id=3): 한우 갈비(6), 된장찌개(7), 그릴드 야채(2)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(3, 6), (3, 7), (3, 2);

-- 삼겹살 구이 세트 (id=4): 삼겹살(8), 상추쌈(9), 된장찌개(7)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(4, 8), (4, 9), (4, 7);

-- 연어 스시 모듬 (id=5): 연어 스시(10), 참치 스시(11)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(5, 10), (5, 11);

-- Dinner-ServingStyle relationships (all dinners have all styles available)
INSERT INTO dinner_serving_styles (dinner_id, style_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3),
(4, 1), (4, 2), (4, 3),
(5, 1), (5, 2), (5, 3);

-- Test users
-- User account (password: test1234)
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('user@test.com', '$2a$10$YQs0I8K7QGQQJYgz3QZ7J.0.6MXzGg5UZ4Y/7ViQdQN0lqZ6YK9Hm', '테스트 사용자', '서울시 강남구 테헤란로 123', '010-1234-5678', 'USER', NOW(), NOW()),
('김철수@test.com', '$2a$10$YQs0I8K7QGQQJYgz3QZ7J.0.6MXzGg5UZ4Y/7ViQdQN0lqZ6YK9Hm', '김철수', '서울시 서초구 강남대로 456', '010-2222-3333', 'USER', NOW(), NOW()),
('이영희@test.com', '$2a$10$YQs0I8K7QGQQJYgz3QZ7J.0.6MXzGg5UZ4Y/7ViQdQN0lqZ6YK9Hm', '이영희', '서울시 송파구 올림픽로 789', '010-3333-4444', 'USER', NOW(), NOW()),
('박민수@test.com', '$2a$10$YQs0I8K7QGQQJYgz3QZ7J.0.6MXzGg5UZ4Y/7ViQdQN0lqZ6YK9Hm', '박민수', '서울시 강동구 천호대로 321', '010-4444-5555', 'USER', NOW(), NOW());

-- Staff account (password: staff1234)
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('staff@test.com', '$2a$10$zQs8J9L7RHRRKZhz4Ra8K.1.7NYaHh6VA5Z/8WjReSO1mrb7ZL0In', '직원 계정', '서울시 강남구 테헤란로 456', '010-9876-5432', 'STAFF', NOW(), NOW());

-- Carts for users
INSERT INTO carts (customer_id) VALUES
(1),  -- user@test.com의 장바구니
(2),  -- 김철수의 장바구니
(3);  -- 이영희의 장바구니

-- Cart Items (샘플 장바구니 데이터)
-- user@test.com의 장바구니: 프리미엄 스테이크 디너 x2 (프리미엄 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(1, 1, 2, 2);

-- 김철수의 장바구니: 시푸드 파스타 세트 x1 (심플 스타일), 연어 스시 모듬 x1 (패밀리 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(2, 2, 1, 1),
(2, 5, 3, 1);

-- 이영희의 장바구니: 삼겹살 구이 세트 x3 (패밀리 스타일)
INSERT INTO cart_items (cart_id, dinner_id, serving_style_id, quantity) VALUES
(3, 4, 3, 3);

-- Sample Orders (샘플 주문 데이터)
-- 박민수의 완료된 주문
INSERT INTO orders (customer_id, status, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(4, 'DELIVERED', '서울시 강동구 천호대로 321', TIMESTAMP '2025-11-17 19:00:00', 165000, TIMESTAMP '2025-11-16 15:30:00', TIMESTAMP '2025-11-17 19:30:00');

-- 김철수의 배달 중인 주문
INSERT INTO orders (customer_id, status, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(2, 'DELIVERING', '서울시 서초구 강남대로 456', TIMESTAMP '2025-11-18 18:00:00', 90000, TIMESTAMP '2025-11-18 14:00:00', TIMESTAMP '2025-11-18 16:30:00');

-- 이영희의 조리 중인 주문
INSERT INTO orders (customer_id, status, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(3, 'IN_KITCHEN', '서울시 송파구 올림픽로 789', TIMESTAMP '2025-11-18 20:00:00', 110000, TIMESTAMP '2025-11-18 16:00:00', TIMESTAMP '2025-11-18 16:15:00');

-- user@test.com의 접수된 주문
INSERT INTO orders (customer_id, status, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(1, 'RECEIVED', '서울시 강남구 테헤란로 123', TIMESTAMP '2025-11-18 19:30:00', 86000, TIMESTAMP '2025-11-18 17:00:00', TIMESTAMP '2025-11-18 17:05:00');

-- 김철수의 재고 확인 중인 주문
INSERT INTO orders (customer_id, status, delivery_address, delivery_date, total_price, created_at, updated_at) VALUES
(2, 'CHECKING_STOCK', '서울시 서초구 강남대로 456', TIMESTAMP '2025-11-19 12:00:00', 175000, TIMESTAMP '2025-11-18 17:30:00', TIMESTAMP '2025-11-18 17:30:00');

-- Order Items (주문 아이템 데이터)
-- 주문 1 (박민수, DELIVERED): 한우 갈비 정식 x3 (프리미엄 스타일)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(1, 3, 2, 3, 165000);

-- 주문 2 (김철수, DELIVERING): 프리미엄 스테이크 디너 x1 (프리미엄), 연어 스시 모듬 x1 (심플)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(2, 1, 2, 1, 55000),
(2, 5, 1, 1, 38000);

-- 주문 3 (이영희, IN_KITCHEN): 시푸드 파스타 세트 x2 (프리미엄)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(3, 2, 2, 2, 110000);

-- 주문 4 (user@test.com, RECEIVED): 삼겹살 구이 세트 x2 (패밀리)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(4, 4, 3, 2, 86000);

-- 주문 5 (김철수, CHECKING_STOCK): 프리미엄 스테이크 디너 x2 (패밀리), 한우 갈비 정식 x1 (심플)
INSERT INTO order_items (order_id, dinner_id, serving_style_id, quantity, price) VALUES
(5, 1, 3, 2, 120000),
(5, 3, 1, 1, 55000);
