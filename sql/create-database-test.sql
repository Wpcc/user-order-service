CREATE DATABASE IF NOT EXISTS user_order_service_test
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE user_order_service_test;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    product_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_orders_user_status_created_at (user_id, status, created_at)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    product_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal_amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_items_order_id (order_id)
) ENGINE = InnoDB;

INSERT INTO users (id, username)
VALUES (1, 'test-user')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO products (id, name, price, stock)
VALUES (1, 'Test Mechanical Keyboard', 299.00, 100)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    price = VALUES(price),
    stock = VALUES(stock);

INSERT INTO orders (
    id, user_id, product_id, product_name, product_price,
    quantity, total_amount, status
)
VALUES (
    1, 1, 1, 'Test Mechanical Keyboard', 299.00,
    2, 598.00, 'PAID'
)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    product_id = VALUES(product_id),
    product_name = VALUES(product_name),
    product_price = VALUES(product_price),
    quantity = VALUES(quantity),
    total_amount = VALUES(total_amount),
    status = VALUES(status);

INSERT INTO order_items (
    id, order_id, product_id, product_name, product_price,
    quantity, subtotal_amount
)
VALUES (
    1, 1, 1, 'Test Mechanical Keyboard', 299.00,
    2, 598.00
)
ON DUPLICATE KEY UPDATE
    order_id = VALUES(order_id),
    product_id = VALUES(product_id),
    product_name = VALUES(product_name),
    product_price = VALUES(product_price),
    quantity = VALUES(quantity),
    subtotal_amount = VALUES(subtotal_amount);