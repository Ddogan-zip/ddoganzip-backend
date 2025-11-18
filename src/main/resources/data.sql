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
('user@test.com', '$2a$10$YQs0I8K7QGQQJYgz3QZ7J.0.6MXzGg5UZ4Y/7ViQdQN0lqZ6YK9Hm', '테스트 사용자', '서울시 강남구 테헤란로 123', '010-1234-5678', 'USER', NOW(), NOW());

-- Staff account (password: staff1234)
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('staff@test.com', '$2a$10$zQs8J9L7RHRRKZhz4Ra8K.1.7NYaHh6VA5Z/8WjReSO1mrb7ZL0In', '직원 계정', '서울시 강남구 테헤란로 456', '010-9876-5432', 'STAFF', NOW(), NOW());
