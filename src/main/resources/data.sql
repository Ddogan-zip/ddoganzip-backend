-- Serving Styles
INSERT INTO serving_styles (name, additional_price, description) VALUES
('Simple', 0, '플라스틱 접시와 컵, 종이 냅킨'),
('Grand', 5000, '도자기 접시와 컵, 흰색 면 냅킨'),
('Deluxe', 10000, '꽃병, 도자기 접시와 컵, 린넨 냅킨');

-- Dishes
INSERT INTO dishes (name, default_quantity) VALUES
('Steak', 1),
('Wine', 1),
('Coffee', 1),
('Salad', 1),
('Egg Scramble', 1),
('Bacon', 1),
('Baguette', 1),
('Champagne', 2);

-- Dinners
INSERT INTO dinners (name, description, base_price) VALUES
('Valentine Dinner', '하트 모양과 큐피드 장식이 포함된 로맨틱한 저녁 식사', 35000),
('French Dinner', '커피, 와인, 샐러드, 스테이크로 구성된 프랑스식 정찬', 30000),
('English Dinner', '에그 스크램블, 베이컨, 빵, 스테이크로 구성된 영국식 저녁', 28000),
('Champagne Feast Dinner', '2인 식사, 샴페인, 바게트빵, 커피, 와인, 스테이크가 포함된 호화로운 만찬', 80000);

-- Dinner-Dish relationships
-- Valentine Dinner (id=1): Steak(1), Wine(2), Coffee(3), Salad(4)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4);

-- French Dinner (id=2): Steak(1), Wine(2), Coffee(3), Salad(4)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4);

-- English Dinner (id=3): Steak(1), Egg Scramble(5), Bacon(6), Baguette(7)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(3, 1), (3, 5), (3, 6), (3, 7);

-- Champagne Feast Dinner (id=4): Steak(1), Wine(2), Coffee(3), Baguette(7), Champagne(8)
INSERT INTO dinner_dishes (dinner_id, dish_id) VALUES
(4, 1), (4, 2), (4, 3), (4, 7), (4, 8);

-- Dinner-ServingStyle relationships (all dinners have all styles available)
INSERT INTO dinner_serving_styles (dinner_id, style_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3),
(4, 1), (4, 2), (4, 3);

-- Sample staff user (password: staff123)
INSERT INTO customers (email, password, name, address, phone, role, created_at, updated_at) VALUES
('staff@ddoganzip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iWYMYB6Z1HvQvqoL/KN4LSYlPFoy', 'Staff User', '서울시 강남구', '010-1234-5678', 'STAFF', NOW(), NOW());
