-- Drop tables if exist (for clean restart)
DROP TABLE IF EXISTS customization_actions CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS dinner_serving_styles CASCADE;
DROP TABLE IF EXISTS dinner_dishes CASCADE;
DROP TABLE IF EXISTS dinners CASCADE;
DROP TABLE IF EXISTS dishes CASCADE;
DROP TABLE IF EXISTS serving_styles CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- Customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Serving Styles table
CREATE TABLE serving_styles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    additional_price INTEGER NOT NULL,
    description VARCHAR(500)
);

-- Dishes table
CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    base_price INTEGER NOT NULL,
    default_quantity INTEGER NOT NULL,
    current_stock INTEGER DEFAULT 0,
    minimum_stock INTEGER DEFAULT 10
);

-- Dinners table
CREATE TABLE dinners (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    base_price INTEGER NOT NULL,
    image_url VARCHAR(500)
);

-- Dinner-Dish relationship (many-to-many)
CREATE TABLE dinner_dishes (
    dinner_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    PRIMARY KEY (dinner_id, dish_id),
    FOREIGN KEY (dinner_id) REFERENCES dinners(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
);

-- Dinner-ServingStyle relationship (many-to-many)
CREATE TABLE dinner_serving_styles (
    dinner_id BIGINT NOT NULL,
    style_id BIGINT NOT NULL,
    PRIMARY KEY (dinner_id, style_id),
    FOREIGN KEY (dinner_id) REFERENCES dinners(id) ON DELETE CASCADE,
    FOREIGN KEY (style_id) REFERENCES serving_styles(id) ON DELETE CASCADE
);

-- Carts table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Cart Items table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    dinner_id BIGINT NOT NULL,
    serving_style_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (dinner_id) REFERENCES dinners(id) ON DELETE CASCADE,
    FOREIGN KEY (serving_style_id) REFERENCES serving_styles(id) ON DELETE CASCADE
);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    delivery_date TIMESTAMP,
    delivery_address VARCHAR(500),
    total_price INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Order Items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    dinner_id BIGINT NOT NULL,
    serving_style_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price INTEGER NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dinner_id) REFERENCES dinners(id) ON DELETE CASCADE,
    FOREIGN KEY (serving_style_id) REFERENCES serving_styles(id) ON DELETE CASCADE
);

-- Customization Actions table
CREATE TABLE customization_actions (
    id BIGSERIAL PRIMARY KEY,
    cart_item_id BIGINT,
    order_item_id BIGINT,
    action VARCHAR(50),
    dish_id BIGINT,
    quantity INTEGER,
    FOREIGN KEY (cart_item_id) REFERENCES cart_items(id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
);
