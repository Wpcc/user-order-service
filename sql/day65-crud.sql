USE user_order_service;

-- 1.新增用户和商品
INSERT INTO users (username)
VALUES ('alices');

INSERT INTO products (name,price,stock)
VALUES ('Mechaincal Keyboard', 299.00,100);

-- 2.查看新增数据，确认id
SELECT * FROM users;
SELECT * FROM products;

-- 假设 alice 和 Mechanical Keyboard 的 id 都是1

-- 3.模拟下单前扣减库存：只有库存不少于2 才扣减
UPDATE products
SET stock = stock - 2
WHERE id = 1 AND stock >= 2;

-- 4.新增订单
INSERT INTO orders (
    user_id,
    product_id,
    product_name,
    product_price,
    quantity,
    total_amount,
    status
)
VALUES (1, 1, 'Mechanical Keyboard', 299.00, 2, 598.00, 'PENDING');

-- 5.查询订单，并关联查询用户名和商品名
SELECT
  o.id,
  u.username,
  o.product_name AS ordered_product_name,
  o.product_price AS ordered_product_price,
  p.name AS current_product_name,
  p.price AS current_product_price,
  o.quantity,
  o.total_amount,
  o.status,
  o.created_at
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN products p ON o.product_id = p.id;

-- 6.更新订单状态
UPDATE orders
SET status = 'PAID'
WHERE id = 1;

SELECT * FROM orders WHERE id = 1;

-- 7.删除一条零时商品，练习DELETE
INSERT INTO products (name,price,stock)
VALUES ('Temporary Item',1.00,1);

DELETE FROM products
WHERE name = 'Temporary Item';