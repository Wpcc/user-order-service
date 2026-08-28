USE user_order_service;

-- 为已有 orders 表新增联合索引，并移除被其覆盖的旧索引
ALTER TABLE orders
    ADD INDEX idx_orders_user_status_created_at (user_id, status, created_at),
    DROP INDEX idx_orders_user_id;

-- 查看当前索引
SHOW INDEX FROM orders;

-- 按用户、状态查询订单，并按创建时间倒序排列
EXPLAIN
SELECT *
FROM orders
WHERE user_id = 1
  AND status = 'PAID'
ORDER BY created_at DESC;

-- 对比：跳过联合索引最左侧 user_id 的查询
EXPLAIN
SELECT *
FROM orders
WHERE status = 'PAID'
ORDER BY created_at DESC;