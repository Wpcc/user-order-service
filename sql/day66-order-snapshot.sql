USE user_order_service;

ALTER TABLE orders
    ADD COLUMN product_name VARCHAR(128) NOT NULL DEFAULT '' AFTER product_id,
    ADD COLUMN product_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER product_name;

UPDATE orders o
JOIN products p ON o.product_id = p.id
SET
    o.product_name = p.name,
    o.product_price = p.price;

ALTER TABLE orders
    MODIFY COLUMN product_name VARCHAR(128) NOT NULL,
    MODIFY COLUMN product_price DECIMAL(10, 2) NOT NULL;